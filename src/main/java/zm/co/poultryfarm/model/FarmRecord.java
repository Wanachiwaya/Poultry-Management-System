package zm.co.poultryfarm.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "recordType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FeedRecord.class, name = "feed"),
    @JsonSubTypes.Type(value = EggProduction.class, name = "eggs"),
    @JsonSubTypes.Type(value = Sale.class, name = "sale"),
    @JsonSubTypes.Type(value = Expense.class, name = "expense")
})
public interface FarmRecord {

    String getId();

    void setId(String id);

    LocalDate getDate();

    String getSummary();

    double getAmount();
}
