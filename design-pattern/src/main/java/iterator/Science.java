package iterator;


import java.util.LinkedList;

public class Science implements ISubject {
    private LinkedList<String> subjects ;
    public Science(){
        subjects  = new LinkedList();
        subjects.add("computer");
        subjects.add("math.");
        subjects.add("algorithm.");
    }
    @Override
    public IIterator iterator() {
        return new ScienceIterator(subjects);
    }
    class ScienceIterator implements IIterator{
        private LinkedList<String>  subjects;
        private int position;
        public ScienceIterator(LinkedList<String>  subjects){
            this.subjects = subjects;
        }
        @Override
        public String current() {
            return subjects.get(position);
        }

        @Override
        public String next() {
            return subjects.get(position++);
        }

        @Override
        public boolean isDone() {
            return position>=subjects.size();
        }

        @Override
        public void fisrt() {
            position = 0;
        }
    }
}
