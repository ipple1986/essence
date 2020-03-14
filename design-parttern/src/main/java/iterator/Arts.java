package iterator;


public class Arts implements ISubject {
    private String[] subjects ;
    public Arts(){
        subjects  = new String[2];
        subjects[0] = "beangle";
        subjects[1] = "english";
    }
    @Override
    public IIterator iterator() {
        return new ArtsIterator(subjects);
    }
    class ArtsIterator implements IIterator{
        private String[] subjects;
        private int position;
        public ArtsIterator(String[] subjects){
            this.subjects = subjects;
        }
        @Override
        public String current() {
            return subjects[position];
        }

        @Override
        public String next() {
            return subjects[position++];
        }

        @Override
        public boolean isDone() {
            return position>=subjects.length;
        }

        @Override
        public void fisrt() {
            position = 0;
        }
    }
}
