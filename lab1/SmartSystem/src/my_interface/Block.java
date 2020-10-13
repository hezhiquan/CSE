package my_interface;

public interface Block
{
    int getIndexId();
    BlockManager getBlockManager();
    byte[] read();
    int blockSize();
}
