package simplenem12.parsers;

import com.opencsv.CSVReader;
import simplenem12.exceptions.ApplicationException;
import simplenem12.model.EnergyUnit;
import simplenem12.model.MeterRead;
import simplenem12.model.MeterVolume;
import simplenem12.model.Quality;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SimpleNem12ParserImpl.class);

    private List<MeterRead> meterReads;

    @Override
    public Collection<MeterRead> parseSimpleNem12(String simpleNem12FilePath) throws ApplicationException {
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " starts");


        csvReader(simpleNem12FilePath);

        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " ends");
        return meterReads;
    }

    public void csvReader(String simpleNem12FilePath) throws ApplicationException {
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " starts");

        try (CSVReader reader = new CSVReader(new FileReader(simpleNem12FilePath))) {
            String[] nextLine;
            for (int i = 0; (nextLine = reader.readNext()) != null; i++) {
                switch (nextLine[0]) {

                    case "100":
                        do100Stuff(nextLine, i);
                        break;

                    case "200":
                        do200Stuff(nextLine[1]);
                        break;

                    case "300":
                        do300Stuff(nextLine);
                        break;

                    case "900":
                        do900Stuff(nextLine);
                        return;
                }

            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " ends");
    }

    private void do900Stuff(String[] nextLine) {
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " starts");


        log.debug("Reached at end of file :: " + nextLine[0]);

        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " ends");
    }

    private void do300Stuff(String[] nextLine) {
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " starts");
        log.debug("New Meter Read record started :: " + nextLine[0]);

        SortedMap vols = meterReads.get(meterReads.size() - 1).getVolumes();

        LocalDate yyyyMMdd = LocalDate.parse(nextLine[1], DateTimeFormatter.ofPattern("yyyyMMdd"));
        BigDecimal bDec = new BigDecimal(nextLine[2]);
        Quality qty = nextLine[3].equals("A") ? Quality.A : Quality.E;

        MeterVolume mVol = new MeterVolume(bDec, qty);

        vols.put(yyyyMMdd, mVol);

        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " ends");
    }

    private void do200Stuff(String nmi) {
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " starts");

        log.debug("New Meter Read Block started :: " + nmi);

        meterReads.add(new MeterRead(nmi, EnergyUnit.KWH));

        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " ends");

    }

    private void do100Stuff(String[] nextLine, int i) throws ApplicationException {
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " starts");

        // validation to check if record type 100 is read after first line
        if (i != 0) {
            log.error("Record Type 100 read at line num # " + i);
            throw new ApplicationException("Record Type 100 read at line num # " + i);
        }

        log.debug("Reached at start of file :: " + nextLine[0]);

        meterReads = new ArrayList();

        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " ends");
    }

    /*private String startsWith(String nextLine) {
        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " starts");
        

        log.trace(Thread.currentThread().getStackTrace()[1].getMethodName() + " ends");
        return nextLine.split(",")[0];
    }*/

}
