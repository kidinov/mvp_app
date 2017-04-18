package org.kidinov.rijksmuseum.test.common;

import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.model.agenda.Exposition;
import org.kidinov.rijksmuseum.data.model.agenda.ExpositionType;
import org.kidinov.rijksmuseum.data.model.agenda.Option;
import org.kidinov.rijksmuseum.data.model.agenda.Period;
import org.kidinov.rijksmuseum.data.model.agenda.Price;
import org.kidinov.rijksmuseum.data.model.collection.ArtObject;
import org.kidinov.rijksmuseum.data.model.collection.Collection;
import org.kidinov.rijksmuseum.data.model.collection.WebImage;
import org.kidinov.rijksmuseum.data.remote.RetrofitException;
import org.kidinov.rijksmuseum.util.C;
import org.kidinov.rijksmuseum.util.DateUtil;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import io.realm.RealmList;

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
public class TestDataFactory {
    public static RetrofitException makeRetrofitException(RetrofitException.Kind kind) {
        return new RetrofitException(randomString(), randomString(), null, kind, new RuntimeException(), null);
    }

    public static Agenda makeRandomAgenda(String date, int countOfItems) {
        Agenda agenda = new Agenda();
        agenda.setAgendaDateString(date);
        agenda.setOptions(makeRandomOptions(countOfItems));
        return agenda;
    }

    private static RealmList<Option> makeRandomOptions(int countOfItems) {
        RealmList<Option> options = new RealmList<>();
        for (int i = 0; i < countOfItems; i++) {
            options.add(makeRandomOption(i));
        }
        return options;
    }

    private static Option makeRandomOption(int id) {
        Option option = new Option();
        option.setId(randomString());
        option.setExposition(makeRandomExposition());
        option.setPeriod(makeRandomPeriod());
        option.setExpositionType(makeRandomExpositionType());
        option.setDate(DateUtil.getDateNDaysDiff(id));
        return option;
    }

    private static ExpositionType makeRandomExpositionType() {
        ExpositionType expositionType = new ExpositionType();
        expositionType.setFriendlyName(randomString());
        return expositionType;
    }

    private static Period makeRandomPeriod() {
        Period period = new Period();
        period.setText(randomString());
        return period;
    }

    private static Exposition makeRandomExposition() {
        Exposition exposition = new Exposition();
        exposition.setDescription(randomString());
        exposition.setPrice(makeRandomPrice());
        return exposition;
    }

    private static Price makeRandomPrice() {
        Price price = new Price();
        price.setAmount((double) randomInt());
        return price;
    }

    public static Agenda makeAgenda(String date, int countOfItems, String pref) {
        Agenda agenda = new Agenda();
        agenda.setAgendaDateString(date);
        agenda.setOptions(makeOptions(countOfItems, pref));
        return agenda;
    }

    private static RealmList<Option> makeOptions(int countOfItems, String pref) {
        RealmList<Option> options = new RealmList<>();
        for (int i = 0; i < countOfItems; i++) {
            options.add(makeOption(i, pref));
        }
        return options;
    }

    private static Option makeOption(int id, String pref) {
        Option option = new Option();
        option.setId(String.valueOf(id));
        option.setExposition(makeExposition(pref, id));
        option.setPeriod(makePeriod(pref, String.valueOf(id)));
        option.setExpositionType(makeExpositionType(pref, String.valueOf(id)));
        option.setDate(DateUtil.getDateNDaysDiff(id));
        return option;
    }

    private static ExpositionType makeExpositionType(String pref, String text) {
        ExpositionType expositionType = new ExpositionType();
        expositionType.setFriendlyName(pref + "_exp_type_" + text);
        return expositionType;
    }

    private static Period makePeriod(String pref, String text) {
        Period period = new Period();
        period.setText(pref + "_period_" + text);
        return period;
    }

    private static Exposition makeExposition(String pref, int price) {
        Exposition exposition = new Exposition();
        exposition.setDescription(pref + "_descr_" + price);
        exposition.setPrice(makePrice(price));
        return exposition;
    }

    private static Price makePrice(int val) {
        Price price = new Price();
        price.setAmount((double) val);
        return price;
    }

    public static Collection makeCollectionWithSeqIds(int countOfItems) {
        Collection collection = new Collection();
        collection.setArtObjects(makeArtObjectsWithSeqIds(countOfItems));
        return collection;
    }

    public static Collection makeCollection(int countOfItems, String prefix) {
        Collection collection = new Collection();
        collection.setMaker(C.COLLECTION_MAKER);
        collection.setLoadingTime(new Date());
        collection.setCount(countOfItems);
        collection.setArtObjects(makeArtObjects(prefix, countOfItems));
        return collection;
    }

    private static RealmList<ArtObject> makeArtObjectsWithSeqIds(int countOfItems) {
        RealmList<ArtObject> artObjects = new RealmList<>();
        for (int i = 0; i < countOfItems; i++) {
            artObjects.add(makeArtObject(i));
        }
        return artObjects;
    }

    private static ArtObject makeArtObject(int id) {
        ArtObject item = new ArtObject();
        item.setFetchOrderNumber(1);
        item.setTitle("title");
        item.setId(String.valueOf(id));
        return item;
    }

    private static RealmList<ArtObject> makeArtObjects(String prefix, int countOfItems) {
        RealmList<ArtObject> artObjects = new RealmList<>();
        for (int i = 0; i < countOfItems; i++) {
            artObjects.add(makeArtObject(prefix, i));
        }
        return artObjects;
    }

    private static ArtObject makeArtObject(String prefix, int i) {
        ArtObject item = new ArtObject();
        item.setFetchOrderNumber(i);
        item.setTitle(prefix + "title" + i);
        item.setId(randomString());
        item.setWebImage(makeArtObjectImage());
        return item;
    }

    private static WebImage makeArtObjectImage() {
        WebImage image = new WebImage();
        image.setUrl(randomString());
        return image;
    }

    private static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    private static String randomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    private static Long randomLong() {
        Random random = new Random();
        return random.nextLong();
    }

    private static Integer randomInt() {
        Random random = new Random();
        return random.nextInt();
    }

}