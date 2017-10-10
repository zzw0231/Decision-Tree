import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
// up to 3 parameters: file name of data set, which column want to predict, frequenc of each data(possible paremeters)
// all data should be in horizontly, use Google Sheet TRANSPOSE function would helps
// jin chen 10/9/2017
public class MAIN {
	public static double total=0;
	public static int predict;
	public static int[] encoded;
	public static int[] wanted;
	public static int[] initwanted;
	public static ArrayList<Integer> encodenumber=new ArrayList<Integer>();
	public static ArrayList<Integer> encodenumberforv=new ArrayList<Integer>();
	public static ArrayList<ArrayList<Double>> templistofi=new ArrayList<ArrayList<Double>>();
	public static ArrayList<ArrayList<Double>> templistofpr=new ArrayList<ArrayList<Double>>();
	public static ArrayList<Integer> numberofchoices=new ArrayList<Integer>();
	public static ArrayList<Integer> currchoice=new ArrayList<Integer>();
	public static ArrayList<ArrayList<String>> alldata=new ArrayList<ArrayList<String>>();
	public static ArrayList<Integer> qorder=new ArrayList<Integer>();
	public static ArrayList<ArrayList<String>> finaldataset=new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<Double>> listofe=new ArrayList<ArrayList<Double>>();
	public static ArrayList<String> allone=new ArrayList<String>();
	public static ArrayList<Double> freq=new ArrayList<Double>();
	public static void main(String[] args) throws IOException {
		String fn=args[0];
		double expect=999;
		predict=Integer.parseInt(args[1]);
		alldata=readInput(fn);
		encoded=new int [alldata.get(0).size()];
		if(args.length>2){
			String fn2=args[2];
			freq=readInput2(fn2);
		}
		else{
			for(int i=0;i<alldata.get(0).size();i++){
				freq.add((double) 1);
			}
		}
		for(int i=0;i<freq.size();i++){
			total+=freq.get(i);
		}

		initwanted=wanted;
		ArrayList<ArrayList<Double>> listofi=new ArrayList<ArrayList<Double>>();

		init(listofi,alldata.size());
		init(listofe,alldata.size());
		int remainsize=alldata.size()-1;
		//ini
		int jnx=(int) (Math.pow(10,alldata.size()));
		for(int i=0;i<alldata.size();i++){
			encodenumber.add(jnx);
			jnx=jnx/10;
			ArrayList<String> templist=(ArrayList<String>) alldata.get(i).clone();
			Set<String> unique = new HashSet<String>(templist);
			numberofchoices.add(unique.size());			
		}//get #of choices
		encodenumber.add(jnx);
		int round=0;
		while((remainsize>0)&&(expect>0)){
			for(int i=0;i<alldata.size();i++){
				if(i!=predict&&(notused(i))){					
					init(templistofi,alldata.size());
					init(templistofpr,alldata.size());
					reinit();
					finaldataset.add(alldata.get(i));
					finaldataset.add(alldata.get(predict));
					currchoice.add(numberofchoices.get(i));
					currchoice.add(numberofchoices.get(predict));
					geti(i);
					System.out.print("Current level: "+(round+1)+" (");
					for(int z=0;z<qorder.size();z++){
						System.out.print("Q"+qorder.get(z)+",");
					}
					System.out.print("Q"+i+"->Predict)");
					double Ev=gete(i);
					System.out.println(" E = "+Ev);
					listofe.get(round).remove(i);
					listofe.get(round).add(i,Ev);
				}					
			}
			expect=getmin(round);
			remainsize--;
			round++;
			listofe=new ArrayList<ArrayList<Double>>();
			init(listofe,alldata.size());
		}//computation loop
		System.out.print("The final path: ");
		for(int i=0;i<qorder.size();i++){
			System.out.print(qorder.get(i)+" ");
		}
	}
	private static void reinit(){
		finaldataset=new ArrayList<ArrayList<String>>();
		currchoice=new ArrayList<Integer>();
		templistofi=new ArrayList<ArrayList<Double>>();
		templistofpr=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<qorder.size();i++){
			finaldataset.add(alldata.get(qorder.get(i)));
			currchoice.add(numberofchoices.get(qorder.get(i)));
		}
		ArrayList<Double> temp=new ArrayList<Double>();
			temp.add((double) 0);
		for(int i=0;i<alldata.size();i++){
			templistofi.add((ArrayList<Double>) temp.clone());
			templistofpr.add((ArrayList<Double>) temp.clone());
		}
	}
	private static double getmin(int round){
		double tempmin=listofe.get(round).get(0);
		int inx=0;
		for(int i=0;i<listofe.get(round).size();i++){
			double t=listofe.get(round).get(i);
			if(t<tempmin){
				tempmin=t;
				inx=i;
			}
		}
		qorder.add(inx);
		System.out.println("Best E = "+tempmin+" inx = Q"+inx);
		System.out.println();
		System.out.println();
		//finaldataset.remove(inx);
		return tempmin;
	}
	private static void iniencode(){
		encodenumberforv=new ArrayList<Integer>();
		int jnx=(int) (Math.pow(10,wanted.length-2));
		for(int i=0;i<wanted.length-2;i++){
			encodenumberforv.add(jnx);
			jnx=jnx/10;
		}
		encodenumberforv.add(jnx);
	}
	
	private static ArrayList<ArrayList<String>> readInput (String nameFile) throws IOException{
		ArrayList<ArrayList<String>> temp=new ArrayList<ArrayList<String>> ();
		FileReader fr = new FileReader(nameFile);
	   BufferedReader input = new BufferedReader(fr);
		String t=null;
		String[] arr=null;
		ArrayList<String> a=new ArrayList<String>();
		while( (t = input.readLine()) != null ){
			//t=input.next();
			arr = t.split("\\s+");
			arr = Arrays.copyOf(arr, arr.length);
			for(int i=0;i<arr.length;i++){		
				a.add(arr[i]);
			}
			ArrayList<String> copy1 = (ArrayList<String>) a.clone();
			temp.add(copy1);
			a.clear();
		}
		input.close();
		return temp;
	}
	private static ArrayList<Double> readInput2 (String nameFile) throws IOException{
		FileReader fr = new FileReader(nameFile);
	   BufferedReader input = new BufferedReader(fr);
		String t=null;
		String[] arr=null;
		ArrayList<Double> a=new ArrayList<Double>();
		while( (t = input.readLine()) != null ){
			//t=input.next();
			arr = t.split("\\s+");
			arr = Arrays.copyOf(arr, arr.length);
			for(int i=0;i<arr.length;i++){		
				double tt=Double.parseDouble(arr[i]);
				a.add(tt);
			}
		}
		input.close();
		return a;
	}
	private static boolean notused(int inx){
		for(int i:qorder){
			if(inx==i){
				return false;
			}
		}
		return true;
	}
	private static void geti(int number){
		int temp=0;
		wanted=new int[qorder.size()+2];
		int[] max=new int[wanted.length-1];
		iniencode();
		for(int i=0;i<qorder.size();i++){
			max[i]=numberofchoices.get(qorder.get(i))-1;
		}
		//temp=temp*numberofchoices.get(number);
		max[max.length-1]=numberofchoices.get(number)-1;
		//temp=temp*numberofchoices.get(predict);
		for(int i=0;i<max.length;i++){
			temp+=max[i]*encodenumberforv.get(i);			
		}
		//while(temp!=0){
			cali(temp,max,number);		
		//	temp--;
		//}
		//
		
	}
	private static void cali(int temp,int[] max,int index){
		int[] curr=new int[wanted.length];
		//while(curr<=temp){
			//for(int i=0;i<wanted.length-1;i++){
			//	curr=wanted[i]*curr;
		//	}
		if(qorder.size()<alldata.size()-2){
		StringBuilder sb=new StringBuilder();
		for(int z=0;z<qorder.size();z++){
			sb.append("Q"+qorder.get(z)+" ");
		}
		sb.append("Q"+index);
		System.out.println(sb);
		}
		else{
			System.out.println("Only one question remain, hide permutation\n");
		}
		while(!(temp<0)){
			/*for(int i=0;i<=wanted.length-2;i++){
					wanted[i]=temp%encodenumber.get(i);
					
					for(int y=0;y<wanted.length-1;y++){
						System.out.print(wanted[y]);
					}
					System.out.println("");
					computeit();
				wanted[i]=0;
			}*/
			vtoa(temp);
			
			if(!Arrays.equals(curr, wanted)&&(allowed(max))){
				curr=wanted.clone();
				if(qorder.size()<alldata.size()-2){
				for(int y=0;y<wanted.length-1;y++){

					System.out.print(" "+wanted[y]+" ");
				}
				System.out.println(" ");
				}
				computeit(index);				
			}
			temp--;
		}
	}
	private static boolean allowed(int[] inx){
		boolean flag=false;
		for(int i=0;i<wanted.length-1;i++){
			if(wanted[i]<=inx[i]) flag=true;
			else return false;
		}
		return flag;
	}
	private static int atov(int[] a){
		int temp=0;
		for(int i=0;i<a.length;i++){
			temp+=a[i]*encodenumberforv.get(i);
		}
		return temp;
	}
	private static void vtoa(int v){
		//wanted[wanted.length-2]=v%10;
		//v=v-wanted[wanted.length-2];
		for(int i=wanted.length-2;i>=0;i--){
			wanted[i]=v%10;
			v-=wanted[i];
			v=v/10;
		}
		//wanted[0]=v%10;
		
	}
	private static void computeit(int index){
		int encodednumber=0;
		double countofbot=0;
		double pr=0;
		encode(finaldataset.size()-1);
		double iv=0;
		for(int i=0;i<wanted.length-1;i++){
			encodednumber+=wanted[i]*encodenumber.get(i);
		}
		for(int i=0;i<encoded.length;i++){
			if(encoded[i]==encodednumber){
				countofbot+=freq.get(i);
			}
		}
		double pr2=countofbot/total;
		
		encode(finaldataset.size());
		for(int i=numberofchoices.get(predict)-1;i>=0;i--){
			encodednumber=0;
			double count=0;
			wanted[wanted.length-1]=i;
			for(int j=0;j<wanted.length;j++){
				encodednumber+=wanted[j]*encodenumber.get(j);
			}
			for(int z=0;z<encoded.length;z++){
				if(encoded[z]==encodednumber){
					count+=freq.get(z);
				}
			}
			

			if(countofbot!=0){
			pr=(count/countofbot);
			//pr=pr/alldata.get(0).size();
			double t1=Math.log10(pr)/Math.log10(2);
			double t=(pr*(t1));
			if(!Double.isNaN(t)&&(t!=0))	iv=iv-t;
			}
			else iv=9999;
		}	
		//if((!Double.isNaN(iv))&&(!Double.isInfinite(iv))){
		if(iv!=9999){
		templistofi.get(index).add(iv);
		//System.out.println(zzz);
		templistofpr.get(index).add(pr2);
		}
		
	}
	private static double gete(int j){
		double result=0;
		for(int i=0;i<templistofi.get(j).size();i++){
			result=templistofpr.get(j).get(i)*templistofi.get(j).get(i)+result;
		}
		return result;
		
	}
	private static  void init(ArrayList<ArrayList<Double>> twodlist,int size){
		ArrayList<Double> temp=new ArrayList<Double>();
		for(int i=0;i<size;i++){
			temp.add((double) 999);
		}
		for(int i=0;i<size;i++){
			twodlist.add((ArrayList<Double>) temp.clone());
		}
	}
	private static void encode(int inx){
		encoded=new int[finaldataset.get(0).size()];
		for(int i=0;i<inx;i++){
			for(int j=0;j<finaldataset.get(i).size();j++){
				int t=Integer.parseInt(finaldataset.get(i).get(j))*encodenumber.get(i);
				encoded[j]=encoded[j]+t;			
			}
		}
	}

}
