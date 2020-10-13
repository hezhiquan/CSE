package my_interface;

public interface BlockManager
{
    Block getBlock(int indexId);
    Block newBlock(byte[] b);
    default Block newEmptyBlock(int blockSize){
        return newBlock(new byte[blockSize]);
    }
}

