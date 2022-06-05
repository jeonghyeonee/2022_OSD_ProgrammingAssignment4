public class FIFO extends PhysicalMemory {
    public int pointer = 0;

    /**
     * Constructor
     *
     * @param size
     */
    public FIFO(int size) {
        super(size);
    }

    public int addFrame(Frame f){
        frames[this.pointer] = new Frame(f.data);
        pointer++;
        currentFreeFrame++;
        return (pointer - 1);
    }
}
