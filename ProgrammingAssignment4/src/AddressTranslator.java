// This implementation is for Lab07 in the Operating System courses in SeoulTech
// The original version of this implementation came from UGA

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class AddressTranslator {


        public static void main(String[] args) throws IOException {
                //String inputFile = args[0];
                String inputFile = "InputFile.txt";


                /**
                 * variable of logical address
                 */
                int addr;


                /**
                 * variable of page number
                 */
                int p_num;


                /**
                 * variable of offset
                 */
                int offset;


                /**
                 * variable of frame number
                 */
                int f_num;


                /**
                 * variable of value stored in address
                 */
                int value;


                /**
                 * variable of physics address
                 */
                int phy_addr;


                /**
                 * variable of count of tlb miss
                 */
                int tlb_miss = 0;


                /**
                 * variable of count of page fault
                 */
                int page_fault = 0;

				/**
				 * variable of rate of TLB miss
				 */
				double tlb_rate = 0.0;

				/**
			 	*  variable of rate of page fault
			 	*/
				double page_rate = 0.0;

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

				System.out.println("which Algorithm do you want?");
				System.out.println("1. FIFO\t\t\t2.LRU");
				System.out.println("-------------------------------------------------");
				int N = Integer.parseInt(br.readLine());

//				System.out.println("The physical memory size is 128");
				int p_size = 128;



                try{
                	Scanner sc = new Scanner(new File(inputFile));

                	TLB tlb = new TLB();
                	PageTable pt = new PageTable();
                	PhysicalMemory pm = new PhysicalMemory(p_size);
                	BackStore bs = new BackStore();
                	FIFO fifo = new FIFO(p_size);
                	LRU lru = new LRU(p_size);

                	int total = 0;



                	while(sc.hasNextInt()){
                		total++;
                		addr = sc.nextInt();
                		// 2^16 = 4^8 = 16^4
                		// mask the high 16bit
                		addr = addr % 65536;
                		offset = addr % 256;
                		p_num = addr / 256;


                		f_num = -1;
                		f_num = tlb.get(p_num);

                		if(f_num == -1){
                			tlb_miss++;
                			// frame not in TLB
                			// try page table
                			f_num = pt.get(p_num);


                			if(f_num == -1){

                				page_fault++;
                				// fraem not in page table
                				// read frame from BackStore
                				Frame f = new Frame(bs.getData(p_num));

                				// FIFO
                				if(N == 1){
                					if(pm.currentFreeFrame == pm.frames.length){
                						fifo.pointer = fifo.pointer % pm.frames.length;
                						pm.currentFreeFrame--;
                						f_num = fifo.addFrame(f);

                						for(int i=0; i<pt.table.length; i++){
                							if(pt.table[i].getFrameNumber() == f_num){
                								pt.table[i].valid = false;
                								pt.table[i].frameNumber = -1;
											}
										}
									}
                					else{
                						f_num = pm.addFrame(f);
									}
									pt.add(p_num, f_num);
									tlb.put(p_num, f_num);
								}

//                				LRU
                				else if(N == 2){
                					if(pm.currentFreeFrame == pm.frames.length){
										int vp = lru.Victim(p_num,true);
										int vf = pt.table[vp].getFrameNumber();
										// set page table invalid
										pt.table[vp].valid = false;
										pt.table[vp].frameNumber=-1;

										pm.currentFreeFrame--;

										try {
											f_num = lru.addFrame(f, vf);
										}catch(Exception e) {
											f_num = pm.addFrame(f);
										}

										pt.add(p_num, f_num);
										tlb.put(p_num, f_num);
									}
									else{
										// add frame to PhysicalMemory
										lru.Victim(p_num, false);
										f_num = pm.addFrame(f);
										pt.add(p_num, f_num);
										tlb.put(p_num, f_num);
									}
								}
                			}
                			else{
								lru.Victim(p_num, false);
							}
                		}
                		else{
							lru.Victim(p_num, false);
						}


		                phy_addr = f_num * 256 + offset;
                		value = pm.getValue(f_num, offset);


                       System.out.println(
                    		   String.format("Virtual address: %s Physical address: %s Value: %s", addr, phy_addr , value)
                    	);
                	}

					tlb_rate = tlb_miss/(double)total;
                	page_rate = page_fault/(double)total;

                	System.out.println(String.format("TLB miss: %s, Page Fault: %s", tlb_miss, page_fault));

					System.out.println(String.format("TLB miss Rate: %f, Page Fault Rate: %f", tlb_rate, page_rate));

                } catch(Exception e){
                e.printStackTrace();
                System.exit(0);
                }
        	}
}

