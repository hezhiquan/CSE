package implement;

import my_interface.Block;
import my_interface.File;
import my_interface.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileImpl implements File, Serializable
{
    private static final long serialVersionUID = 3350352586693142130L;
    private String filename;
    private int fileSize;
    private String fileManager;
    private int blockSize;
    private long cursor;
    private String[][] blockLists;

    public FileImpl(String fileManager, String filename, int fileSize)
    {
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileManager = fileManager;
        this.cursor = 0;

    }

    @Override
    public Id getFileId()
    {
        return new Id(fileManager, filename);
    }

    public String getFilename()
    {
        return filename;
    }

    @Override
    public FileManager getFileManager()
    {
        return FileManagerImpl.getFileManager();
    }

    public String getFM()
    {
        return fileManager;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public void setFileManager(String fileManager)
    {
        this.fileManager = fileManager;
    }

    @Override
    public byte[] read(int length)
    {
        if (cursor + length > fileSize || cursor + length < 0)
        {
            throw new ErrorCodeException(ErrorCodeException.CURSOR_OUT_OF_BOUND);
        }
        long index = getCursor(); //获取光标位置
        String res = new String(getFileData());
        setCursor(index + length); //更新光标位置
        if (res.length() == 0)
        {
            return "".getBytes();
        }
        return res.substring(0, length).getBytes();
    }


    public byte[] read2(int length)
    {
        if (cursor + length > fileSize || cursor + length < 0)
        {
            throw new ErrorCodeException(ErrorCodeException.CURSOR_OUT_OF_BOUND);
        }
        int oldCursor = (int) cursor;
        //先算出起点所在的块号和偏移，再算出终点所在的块号和偏移
        int blockNum1 = oldCursor / blockSize + 1;
        int offset1 = oldCursor % blockSize;

        int blockNum2 = (oldCursor + length) / blockSize + 1;
        int offset2 = (oldCursor + length) % blockSize;
        //如果起点终点在同一个块内，则只取该块的数据，否则取二者之间的数据
        BlockManagerImpl blockManager = BlockManagerImpl.getBlockManager();
        if (blockNum2 == blockNum1)
        {
            Block block = blockManager.getBlock(blockLists[blockNum1][0], Integer.parseInt(blockLists[blockNum1][1]));
            //更新cursor位置
            cursor += length;
            return Arrays.copyOfRange(block.read(), offset1, offset2);
        }
        else
        {
            ArrayList<byte[]> arrayList = new ArrayList<>();
            int allLength = 0;
            for (int i = blockNum1; i < blockNum2 + 1; i++)
            {
                byte[] data = blockManager.getBlock(blockLists[i][0], Integer.parseInt(blockLists[i][1])).read();
                //如果是第一个或者是最后一个块，取出对应的长度，其它的取全部
                if (i == blockNum1)
                {
                    byte[] firstBlockData = Arrays.copyOfRange(data, offset1, data.length);
                    allLength += firstBlockData.length;
                    arrayList.add(firstBlockData);
                }
                else if (i == blockNum2)
                {
                    byte[] lastBlockData = Arrays.copyOfRange(data, 0, offset2);
                    allLength += lastBlockData.length;
                    arrayList.add(lastBlockData);
                }
                else
                {
                    allLength += data.length;
                    arrayList.add(data);
                }
            }
            //更新cursor位置
            cursor += length;
            return arraylist2Bytes(arrayList, allLength);
        }
    }

    public int getBlockSize()
    {
        return blockSize;
    }

    public void setBlockSize(int blockSize)
    {
        this.blockSize = blockSize;
    }

    public String[][] getBlockLists()
    {
        return blockLists;
    }

    @Override
    public void write(byte[] content)
    {
        BlockManagerImpl blockManager = BlockManagerImpl.getBlockManager();
        setFileSize(content.length);
        setBlockSize(blockManager.getBlockSize());

        String[][] blockInFile = blockManager.allocateBlock(content);
        setBlockLists(blockInFile);
        java.io.File file = new java.io.File("src/" + this.fileManager + "/" + this.filename + ".meta");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file))))
        {
            objectOutputStream.writeObject(this);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("写入文件失败");
        }
    }

    public void setBlockLists(String[][] blockLists)
    {
        this.blockLists = blockLists;
    }

    @Override
    public long move(long offset, int where)
    {
        switch (where)
        {
            case MOVE_CURR:
                if (cursor + offset > fileSize || cursor + offset < 0)
                {

                    throw new ErrorCodeException(ErrorCodeException.CURSOR_OUT_OF_BOUND);
                }
//                System.out.println("当前文件大小为"+fileSize+"光标为 "+cursor);
                cursor = cursor + offset;
                break;
            case MOVE_HEAD:
                if (offset < 0 || offset > fileSize)
                {

                    throw new ErrorCodeException(ErrorCodeException.CURSOR_OUT_OF_BOUND);
                }

                cursor = offset;
//                System.out.println("当前光标为"+cursor);
                break;
            case MOVE_TAIL:
                if (offset < 0 || offset > fileSize)
                {

                    throw new ErrorCodeException(ErrorCodeException.CURSOR_OUT_OF_BOUND);
                }

                cursor = fileSize - offset;
//                System.out.println("当前光标为"+cursor);
                break;
            default:
                System.out.println("where值错误");
        }
        return cursor;
    }

    @Override
    public void close()
    {
        BufferBlockManager bufferBlockManager = BufferBlockManager.getBufferBlockManager();
        bufferBlockManager.clear();
    }

    @Override
    public long size()
    {
        return fileSize;
    }

    /**
     * 修改文件大小
     *
     * @param newSize 新的文件大小
     */
    @Override
    public void setSize(long newSize)
    {
        if(newSize<0){
            System.out.println("文件大小不能为负数");
            return;
        }
        //注意，每个块都有一个备份块
        if (newSize < fileSize)
        {
            if (newSize == 0)
            {
                blockLists = null;
            }
            else
            {
                //删除file meta中的块信息
                int beforeBlockNum = ((int) newSize) / blockSize;
                int remainder = ((int) newSize) % blockSize;
                if (remainder == 0)
                {
                    blockLists = Arrays.copyOf(blockLists, 2 * beforeBlockNum);
                }
                else
                {
                    //比之前多复制2个block，之后得到新的block信息时再把这2个block更新掉
                    blockLists = Arrays.copyOf(blockLists, 2 * (beforeBlockNum + 1));
                    BlockManagerImpl blockManager = BlockManagerImpl.getBlockManager();
                    String[] lastBlock = blockLists[blockLists.length - 1];
                    byte[] content = blockManager.getBlock(lastBlock[0], Integer.parseInt(lastBlock[1])).read();
                    byte[] newContent = Arrays.copyOf(content, remainder);
                    String[][] newBlockInfo = blockManager.allocateBlock(newContent);
                    //更新刚分配的新块和副本块
                    blockLists[blockLists.length - 2] = newBlockInfo[0];
                    blockLists[blockLists.length - 1] = newBlockInfo[1];
                }
            }
        }
        else if (newSize > fileSize)
        {
            int remainder = fileSize % blockSize;
            BlockManagerImpl blockManager = BlockManagerImpl.getBlockManager();
            //如果原文件数据正好能完全填充块，则不做改变，否则，将最后的块填充缺少的0x00
            if (remainder != 0)
            {
                //最后一个块的位置，该块就是一个备份块
                int lastBlockNum = blockLists.length - 1;
                byte[] bytes = blockManager.getBlock(blockLists[lastBlockNum][0], Integer.parseInt(blockLists[lastBlockNum][1])).read();
                //计算原文件最后一个块应填充的数据大小
                int lastFill;
                if (newSize > (blockLists.length / 2 * blockSize))
                {
                    lastFill = blockSize;
                }
                else
                {
                    lastFill = ((int) newSize) - ((lastBlockNum + 1) * blockSize + bytes.length);
                }
                byte[] newContent = fillEmptyData(lastFill, bytes);
                String[][] newLastBlockInfo = blockManager.allocateBlock(newContent);
                //更新刚分配的新块和副本块
                blockLists[lastBlockNum - 1] = newLastBlockInfo[0];
                blockLists[lastBlockNum] = newLastBlockInfo[1];
            }
            if (newSize > (blockLists.length / 2) * blockSize)
            {
                //blockList是原块和副本块的集合，文件内容长度应为其一半
                byte[] newContent = fillEmptyData(((int) newSize) - (blockLists.length / 2 * blockSize), null);
                String[][] newBlockInfo = blockManager.allocateBlock(newContent);
                blockLists = mergeBlockList(blockLists, newBlockInfo);
            }


        }
        else
        {
            //文件大小和新的大小相同，啥也不做
        }
        fileSize = (int) newSize;
        updateFileMeta();
    }

    /**
     * 将新的meta数据保存到file中
     */
    public void updateFileMeta()
    {
        java.io.File file = new java.io.File("src/" + fileManager + "/" + filename + ".meta");
        try
        {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            System.out.println("更新file meta失败");
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            System.out.println("更新file meta失败");
        }

    }

    /**
     * 合并两个string数组
     */
    private String[][] mergeBlockList(String[][] blockLists1, String[][] blockLists2)
    {
        String[][] blockList3 = new String[blockLists1.length + blockLists2.length][2];
        for (int i = 0; i < blockLists1.length; i++)
        {
            blockList3[i] = blockLists1[i];
        }
        for (int i = 0; i < blockLists2.length; i++)
        {
            blockList3[i + blockLists1.length] = blockLists2[i];
        }
        return blockList3;
    }


    /**
     * 输入一个byte数组
     * 返回长度为num，值是byte数组和其后全为0x00的新byte数组
     *
     * @return
     */
    private byte[] fillEmptyData(int num, byte[] content)
    {
        byte[] bytes = new byte[num];
        int length = content == null ? 0 : content.length;
        if (content != null)
        {
            System.arraycopy(content, 0, bytes, 0, length);
        }
        String str = "0";

        byte zero = str.getBytes()[0];
        for (int i = length; i < bytes.length; i++)
        {
            bytes[i] = zero;
        }

        return bytes;
    }


    public void setFileSize(int fileSize)
    {
        this.fileSize = fileSize;
    }

    public void setCursor(long cursor)
    {
        this.cursor = cursor;
    }

    public int getFileSize()
    {
        return fileSize;
    }

    public void insert(byte[] content)
    {
        int index = (int) cursor;
        //得到原文件中index及之后的数据
        byte[] endBytes = getFileData();
        //算出要从第几个块号开始取，从第一个被取到的块的哪个位置开始读
        int beginBlockNum = index / blockSize;
        int remainder = index % blockSize;
        BlockManagerImpl blockManager = BlockManagerImpl.getBlockManager();
        if (remainder != 0)
        {
            //最后一个原块，该块号加1就是它的副本块，即余数所在的块
            int lastBlockNum = beginBlockNum * 2 + 1;
            //读出remainder的数据
            byte[] bytes = blockManager.getBlock(blockLists[lastBlockNum][0], Integer.parseInt(blockLists[lastBlockNum][1])).read();
            byte[] beginBytes = Arrays.copyOf(bytes, remainder);
            //读出未修改的之前的块
            blockLists = Arrays.copyOf(blockLists, beginBlockNum * 2);
            //将beginBytes，content，endBytes三个数组合并成同一个数组
            byte[] tmp = merge3byteArr(beginBytes, content, endBytes);
            String[][] newBlockInfo = blockManager.allocateBlock(tmp);
            blockLists = mergeBlockList(blockLists, newBlockInfo);
        }
        else
        {
            //块能完全装完，则去掉beginBytes这个数组即可
            //读出未修改的之前的块
            blockLists = Arrays.copyOf(blockLists, beginBlockNum * 2);
            //将content，endBytes两个数组合并成同一个数组
            byte[] tmp = mergeBytes(content, endBytes);
            String[][] newBlockInfo = blockManager.allocateBlock(tmp);
            blockLists = mergeBlockList(blockLists, newBlockInfo);
        }
        fileSize += content.length;
        updateFileMeta();
    }

    private byte[] mergeBytes(byte[] data1, byte[] data2)
    {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }

    private byte[] merge3byteArr(byte[] arr1, byte[] arr2, byte[] arr3)
    {
        int length = arr1.length + arr2.length + arr3.length;
        ArrayList<byte[]> arrayList = new ArrayList<>();
        arrayList.add(arr1);
        arrayList.add(arr2);
        arrayList.add(arr3);
        return arraylist2Bytes(arrayList, length);
    }

    public byte[] getFileData()
    {
        if (cursor == fileSize)
        {
            //到达光标尾部，则返回空
            String str = "";
            return str.getBytes();
        }
        int beginPos = (int) this.cursor;
        //算出要从第几个块号开始取，从第一个被取到的块的哪个位置开始读
        int beginBlockNum = beginPos / getBlockSize();
        int remainder = beginPos % getBlockSize();
        BlockManagerImpl blockManager = BlockManagerImpl.getBlockManager();
        //保存所有的byte数组，最后合并所有数组，还原成原来的字符串
        ArrayList<byte[]> arrayList = new ArrayList<>();
        //记录所有byte数组的长度
        int bytesLength = 0;

        for (int i = beginBlockNum * 2; i < blockLists.length; i += 2)
        {
            BlockImpl block = blockManager.getBlock(blockLists[i][0], Integer.parseInt(blockLists[i][1]));
            byte[] data = block.read();
            long newChecksum = blockManager.getChecksum(data);
//            System.out.println(newChecksum+" 新检验和 | 旧检验和 "+block.getChecksum());
            if (newChecksum == block.getChecksum())
            {
                //如果是被取到的第一个块，则从第一个被取到的块的remainder位置开始读
                if (i == beginBlockNum * 2)
                {

                    byte[] catData = new byte[data.length - remainder];
                    for (int j = 0; j < catData.length; j++)
                    {
                        catData[j] = data[j + remainder];
                    }
                    arrayList.add(catData);
                    bytesLength += catData.length;
                }
                else
                {
                    arrayList.add(data);
                    bytesLength += data.length;
                }
            }
            else
            {
//                System.out.println("最近一次的写入失败，开始启用备份");
                BlockImpl backupBlock = blockManager.getBlock(blockLists[i + 1][0], Integer.parseInt(blockLists[i + 1][1]));
                byte[] backupData = backupBlock.read();
                long backupChecksum = blockManager.getChecksum(backupData);
                if (backupChecksum == backupBlock.getChecksum())
                {
//                    System.out.println("备份成功启用");
                    //如果是被取到的第一个块，则从第一个被取到的块的remainder位置开始读

                    if (i == beginBlockNum * 2)
                    {
                        byte[] catData = new byte[backupBlock.getBlockSize() - remainder];
                        for (int j = 0; j < catData.length; j++)
                        {
                            catData[j] = backupData[j + remainder];
                        }
                        arrayList.add(catData);
                        bytesLength += catData.length;
                    }
                    else
                    {
                        arrayList.add(backupData);
                        bytesLength += backupData.length;
                    }


                }
                else
                {
//                    System.out.println("block和备份都不可用");
                    throw new ErrorCodeException(ErrorCodeException.DUPLICATION_UNAVAILABLE);

                }
            }
        }

        //将数组中的byte数组拼接，返回字符串
        return arraylist2Bytes(arrayList, bytesLength);
    }

    public long getCursor()
    {
        return cursor;
    }

    /**
     * 将数组中的byte数组拼接，返回字符串
     *
     * @param arrayList
     * @param length
     * @return
     */
    private byte[] arraylist2Bytes(ArrayList<byte[]> arrayList, int length)
    {
        byte[] bytes = new byte[length];
        int index = 0;
        for (byte[] value : arrayList)
        {
            for (byte b : value)
            {
                bytes[index] = b;
                index++;
            }
        }
        return bytes;
    }

}
