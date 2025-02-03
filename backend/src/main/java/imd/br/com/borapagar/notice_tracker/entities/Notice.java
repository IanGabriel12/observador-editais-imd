package imd.br.com.borapagar.notice_tracker.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    public final static int MAX_LENGTH_DESCRIPTION_IN_CHARACTERES = 600;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true, length = MAX_LENGTH_DESCRIPTION_IN_CHARACTERES)
    private String description;

    @Column(nullable = false)
    private String sourceName;

    @Column(nullable = false)
    private String url;

    @Column
    private Long idAtSource;
}
