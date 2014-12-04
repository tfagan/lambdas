package net.nycjava;

class BabyName {
    final String name;
    final String state;
    final int year;
    final char gender;
    final long number;

    BabyName(String aName, String aState,
             int aYear, char aGender, long aNumber) {
        name = aName;
        state = aState;
        year = aYear;
        gender = aGender;
        number = aNumber;
    }

    long getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return String.format(
            "BabyName{name='%s', state='%s', year=%d, gender=%s, number=%s}",
            name, state, year, gender, number);
    }
}
