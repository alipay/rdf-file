package com.alipay.rdf.file.preheat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alipay.rdf.file.exception.RdfErrorEnum;
import com.alipay.rdf.file.exception.RdfFileException;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileSplitter;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.FileDataTypeEnum;
import com.alipay.rdf.file.model.FileSlice;
import com.alipay.rdf.file.storage.FileInnterStorage;
import com.alipay.rdf.file.util.OssUtil;
import com.alipay.rdf.file.util.RdfFileLogUtil;
import com.alipay.rdf.file.util.RdfFileUtil;
import com.alipay.rdf.file.util.RdfProfiler;

/**
 * Copyright (C) 2013-2018 Ant Financial Services Group
 *
 * oss 预热读处理
 *
 * @author hongwei.quhw
 * @version $Id: OssPreheatFileReaderWrapper.java, v 0.1 2018年4月9日 下午4:49:59 hongwei.quhw Exp $
 */
public class OssPreheatProtocolReaderWrapper {
    private final String                  LOG_PREFIX      = "Rdf-file# oss preheat read, uuid=["
                                                            + UUID.randomUUID() + "]:";
    /**私有线程池*/
    private ThreadPoolExecutor            executor;
    /**监控定时器 */
    private Timer                         monitorTimer;

    private final FileConfig              fileConfig;
    private OssPreheatReaderConfig        preheatConfig;

    private List<FileSlice>               headSliceHoler  = new ArrayList<FileSlice>();
    private List<FileSlice>               tailSliceHolder = new ArrayList<FileSlice>();
    private List<FileSlice>               bodySliceHolders;
    private final SlicesIterator          bodySlicesIterator;

    private final Map<String, DataHolder> preheatData     = new ConcurrentHashMap<String, DataHolder>();;
    private final SlicesIterator          readerIter;
    private DataHolder                    currentDataHolder;
    /**是否中断*/
    protected boolean                     interrupt;

    public OssPreheatProtocolReaderWrapper(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        this.preheatConfig = (OssPreheatReaderConfig) fileConfig
            .getParam(OssPreheatReaderConfig.OSS_PREHEAT_READER_CONFIG_KEY);

        if (null == this.preheatConfig) {
            this.preheatConfig = new OssPreheatReaderConfig();
            if (RdfFileLogUtil.common.isInfo()) {
                RdfFileLogUtil.common.info("rdf-file#没有指定OssPreheatReaderConfig参数，构建默认值");
            }
        }

        // 设置预热读文件路径
        if (preheatConfig.getPaths().isEmpty()) {
            RdfFileUtil.assertNotBlank(fileConfig.getFilePath(),
                "rdf-file#文件路径不能为空, config=" + fileConfig);
            List<String> paths = new ArrayList<String>();
            paths.add(fileConfig.getFilePath());
            preheatConfig.setPaths(paths);
        }

        if (null == preheatConfig.getExecutor()) {
            executor = new ThreadPoolExecutor(preheatConfig.getCorePoolSize(),
                preheatConfig.getMaxPoolSize(), preheatConfig.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(preheatConfig.getBlockingQueueSize()));
        }

        bodySliceHolders = splitAll();
        bodySlicesIterator = new SlicesIterator(bodySliceHolders.iterator());
        RdfFileLogUtil.common.info(LOG_PREFIX + "paths=" + preheatConfig.getPaths()
                                   + ", sliceHolders count=" + bodySliceHolders.size());

        readerIter = new SlicesIterator(bodySliceHolders.iterator());

        fetchData();

        initMonitor();
    }

    public List<DataHolder> readHead() {
        List<DataHolder> headDataHoler = new ArrayList<DataHolder>();
        for (FileSlice headSlice : headSliceHoler) {
            headDataHoler.add(readSliceBytes(headSlice));
        }
        return headDataHoler;
    }

    public List<DataHolder> readTail() {
        List<DataHolder> tailDataHoler = new ArrayList<DataHolder>();
        for (FileSlice tailSlice : tailSliceHolder) {
            tailDataHoler.add(readSliceBytes(tailSlice));
        }
        return tailDataHoler;
    }

    public void close() {
        if (null != monitorTimer) {
            monitorTimer.cancel();
        }

        if (null != executor) {
            executor.shutdown();
        }

        interrupt = true;
        executor = null;
        monitorTimer = null;
    }

    public DataHolder readBodyData() {
        if (preheatData.isEmpty() && readerIter.hasNext()) {
            if (RdfFileLogUtil.common.isDebug()) {
                RdfFileLogUtil.common.debug(LOG_PREFIX + "preheatData为空， 数据消费快，生产慢");
            }
            fetchData();
        }

        getCurrentDataHoder();

        if (null == currentDataHolder) {
            return null;
        }

        DataHolder ret = currentDataHolder;
        preheatData.remove(currentDataHolder.getFileSlice().getKey());
        currentDataHolder = null;
        fetchData();
        return ret;
    }

    private void getCurrentDataHoder() {
        if (null == currentDataHolder) {

            if (readerIter.hasNext()) {
                FileSlice slice = readerIter.next();

                currentDataHolder = preheatData.get(slice.getKey());
                if (null == currentDataHolder) {
                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common
                            .debug(LOG_PREFIX + "getCurrentDataHoder = null, slice="
                                   + slice.getKey() + ", 生产数据慢， 手动直接调度");
                    }
                    readerIter.setBlockFileSlice(slice);
                    fetchSliceData(slice);
                    getCurrentDataHoder();
                }
            } else {
                return;
            }
        }
    }

    private void fetchData() {
        while (bodySlicesIterator.hasNext()) {
            //限流
            if (preheatData.size() > preheatConfig.getCapacity()) {
                if (RdfFileLogUtil.common.isDebug()) {
                    RdfFileLogUtil.common.debug(LOG_PREFIX + "消费慢， 集合数据size=" + preheatData.size()
                                                + ", 限制阈值为" + preheatConfig.getCapacity());
                }
                return;
            }

            final FileSlice sliceHolder = bodySlicesIterator.next();
            try {
                getThreadPoolExecutor().execute(new Runnable() {

                    @Override
                    public void run() {
                        fetchSliceData(sliceHolder);
                    }
                });
            } catch (RejectedExecutionException e) {
                if (RdfFileLogUtil.common.isDebug()) {
                    RdfFileLogUtil.common
                        .debug(LOG_PREFIX + "slice=" + sliceHolder.getKey() + ", 队列满加入失败");
                }
                bodySlicesIterator.setBlockFileSlice(sliceHolder);
                return;
            }
        }
    }

    private void fetchSliceData(FileSlice slice) {
        if (null != preheatData.get(slice.getKey())) {
            return;
        }

        preheatData.put(slice.getKey(), readSliceBytes(slice));
    }

    private DataHolder readSliceBytes(FileSlice slice) {
        if (interrupt) {
            RdfFileLogUtil.common.info(LOG_PREFIX + slice.getKey() + " 任务被关闭，中断获取数据");
            return new DataHolder(new byte[0], slice);
        }

        FileInnterStorage storage = (FileInnterStorage) FileFactory
            .createStorage(fileConfig.getStorageConfig());
        InputStream is = storage.getInputStream(slice.getFilePath(), slice.getStart(),
            slice.getLength());

        return new DataHolder(OssUtil.read(is, (int) slice.getLength()), slice);
    }

    /**
     * 将所有文件进行切分
     * 
     * @return
     */
    protected List<FileSlice> splitAll() {
        RdfProfiler.enter("splitAll start...");
        List<Future<List<FileSlice>>> slicesFutures = new ArrayList<Future<List<FileSlice>>>();
        List<FileSlice> sliceHolders = new ArrayList<FileSlice>();
        for (String path : preheatConfig.getPaths()) {
            final FileConfig sliceConfig = fileConfig.clone();
            sliceConfig.setFilePath(path);
            try {
                slicesFutures.add(getThreadPoolExecutor().submit(new Callable<List<FileSlice>>() {
                    @Override
                    public List<FileSlice> call() throws Exception {
                        return split(sliceConfig);
                    }
                }));
            } catch (RejectedExecutionException e) {
                sliceHolders.addAll(split(sliceConfig));
            }
        }

        for (Future<List<FileSlice>> sliceFuture : slicesFutures) {
            try {
                sliceHolders.addAll(sliceFuture.get());
            } catch (InterruptedException e) {
                RdfFileLogUtil.common.error(LOG_PREFIX + "切分文件出错", e);
                throw new RdfFileException("rdf-file#切分文件出错", e, RdfErrorEnum.DATA_ERROR);
            } catch (ExecutionException e) {
                RdfFileLogUtil.common.error(LOG_PREFIX + "切分文件出错", e);
                throw new RdfFileException("rdf-file#切分文件出错", e, RdfErrorEnum.DATA_ERROR);
            }
        }

        RdfProfiler.release("splitAll end.");

        return sliceHolders;
    }

    private List<FileSlice> split(FileConfig fileConfig) {
        FileSplitter splitter = FileFactory.createSplitter(fileConfig.getStorageConfig());

        if (FileDataTypeEnum.ALL == fileConfig.getFileDataType()
            || FileDataTypeEnum.HEAD == fileConfig.getFileDataType()) {
            try {
                headSliceHoler.add(splitter.getHeadSlice(fileConfig));
            } catch (RdfFileException e) {
                if (e.getErrorEnum().equals(RdfErrorEnum.HEAD_NOT_DEFINED)) {
                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common.debug(e.getMessage(), e);
                    }
                } else {
                    throw e;
                }
            }
        }

        if (FileDataTypeEnum.ALL == fileConfig.getFileDataType()
            || FileDataTypeEnum.TAIL == fileConfig.getFileDataType()) {
            try {
                tailSliceHolder.add(splitter.getTailSlice(fileConfig));
            } catch (RdfFileException e) {
                if (e.getErrorEnum().equals(RdfErrorEnum.TAIL_NOT_DEFINED)) {
                    if (RdfFileLogUtil.common.isDebug()) {
                        RdfFileLogUtil.common.debug(e.getMessage(), e);
                    }
                } else {
                    throw e;
                }
            }
        }

        if (FileDataTypeEnum.ALL != fileConfig.getFileDataType()
            && FileDataTypeEnum.BODY != fileConfig.getFileDataType()) {
            return new ArrayList<FileSlice>(0);
        }

        List<FileSlice> slices = splitter.getBodySlices(fileConfig,
            preheatConfig.getSliceBlockSize());

        if (slices.size() == 1 && slices.get(0).getLength() == 0) {
            RdfFileLogUtil.common.info("rdf-file#文件body没有数据 fileConfig" + fileConfig);
            slices.clear();
        }

        return slices;
    }

    private ThreadPoolExecutor getThreadPoolExecutor() {
        if (null != executor) {
            return executor;
        }

        return preheatConfig.getExecutor();
    }

    private void initMonitor() {

        if (preheatConfig.isMonitorThreadPool()) {
            RdfFileLogUtil.common.info(LOG_PREFIX + "开启监控定时器");
        } else {
            RdfFileLogUtil.common.info(LOG_PREFIX + "不打开监控定时器");
            return;
        }

        monitorTimer = new Timer();

        monitorTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                RdfFileLogUtil.common.info("monitor start...");
                RdfFileLogUtil.common.info(
                    LOG_PREFIX + "执行线程池activeCount=" + getThreadPoolExecutor().getActiveCount());
                RdfFileLogUtil.common.info(
                    LOG_PREFIX + "执行线程池queueSize=" + getThreadPoolExecutor().getQueue().size());
                RdfFileLogUtil.common
                    .info(LOG_PREFIX + "slicesIterator.hasNext=" + bodySlicesIterator.hasNext());
                RdfFileLogUtil.common.info(LOG_PREFIX + "capacity=" + preheatConfig.getCapacity());

                RdfFileLogUtil.common.info(LOG_PREFIX + "preheatData集合size=" + preheatData.size());
                RdfFileLogUtil.common
                    .info(LOG_PREFIX + "readerIter.hasNext=" + readerIter.hasNext());
                RdfFileLogUtil.common.info(
                    LOG_PREFIX + "currentDataHolder is null = " + (currentDataHolder == null));
                RdfFileLogUtil.common.info("monitor end.");
            }

        }, 0, preheatConfig.getMonitorPeriod());
    }

    /**
     * 分片迭代器
     * 
     * @author hongwei.quhw
     * @version $Id: PreheadReader.java, v 0.1 2017年7月11日 下午3:07:41 hongwei.quhw Exp $
     */
    public class SlicesIterator implements Iterator<FileSlice> {
        private final Iterator<FileSlice> sliceIter;
        private FileSlice                 blockFileSlice;

        public SlicesIterator(Iterator<FileSlice> sliceIter) {
            this.sliceIter = sliceIter;
        }

        public void setBlockFileSlice(FileSlice blockFileSlice) {
            this.blockFileSlice = blockFileSlice;
        }

        public FileSlice getBlockFileSlice() {
            return blockFileSlice;
        }

        /** 
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            if (null != blockFileSlice) {
                return true;
            }
            return sliceIter.hasNext();
        }

        /** 
         * @see java.util.Iterator#next()
         */
        @Override
        public FileSlice next() {
            if (null != blockFileSlice) {
                FileSlice temp = blockFileSlice;
                blockFileSlice = null;
                return temp;
            }
            return sliceIter.next();
        }

        /** 
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public class DataHolder {
        private final byte[]    bytes;
        private final FileSlice fileSlice;

        public DataHolder(byte[] bytes, FileSlice fileSlice) {
            this.bytes = bytes;
            this.fileSlice = fileSlice;
        }

        /**
         * Getter method for property <tt>bytes</tt>.
         * 
         * @return property value of bytes
         */
        public byte[] getBytes() {
            return bytes;
        }

        /**
         * Getter method for property <tt>fileSliceHolder</tt>.
         * 
         * @return property value of fileSliceHolder
         */
        public FileSlice getFileSlice() {
            return fileSlice;
        }
    }
}
