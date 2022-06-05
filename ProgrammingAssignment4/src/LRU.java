import java.util.LinkedList;

public class LRU extends PhysicalMemory{
    /**
     * Constructor
     *
     * @param size
     */
    public LRU(int size) {
        super(size);
    }

    LinkedList<Integer> arr = new LinkedList<>();

//  Choose the victim and change it.
    public int Victim(int p_num, boolean b){
       boolean f = true;

       for(int i=0; i<arr.size(); i++){
           if(arr.get(i) == p_num){
               arr.remove(i);
               arr.addLast(p_num);
               f = false;
               break;
           }
       }

       if(f){
           arr.add(p_num);
       }

       if(b){
           int v = arr.poll();
           return v;
       }
       return -1;
    }

    public int addFrame(Frame f, int victim){
        this.frames[victim] = new Frame(f.data);
        this.currentFreeFrame++;
        return victim;
    }


}
