
public class Pair<Word,Num> {
    private Word word;
    private Num num;

    public Pair(Word word, Num num){
        this.word = word;
        this.num = num;
    }

    public Word getWord(){ return this.word; }
    public Num getNum(){ return this.num; }

    public void setWord(Word word){ this.word = word; }
    public void setNum(Num num){ this.num = num; }

    @Override
    public String toString(){
        return String.format("["+this.getWord()+", "+this.getNum()+"]");
    }
}

