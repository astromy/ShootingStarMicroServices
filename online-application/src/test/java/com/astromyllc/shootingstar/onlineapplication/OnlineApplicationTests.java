package com.astromyllc.shootingstar.onlineapplication;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OnlineApplication.class)
@Testcontainers
@NoArgsConstructor
@AutoConfigureMockMvc
class OnlineApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer=new MongoDBContainer("mongo:6.0.8");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private static final Long applicantIndex= 0L;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldSubmitApplication() throws Exception {
	ApplicationRequest applicationRequest=	getApplicationRequest();
   String productRequestString= objectMapper.writeValueAsString(applicationRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/applications/submit-application")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
	}

	private ApplicationRequest getApplicationRequest() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return ApplicationRequest.builder()
				.fatherFirstNames("Peter Jude")
				.fatherLastName("Ackon")
				.fatherEmail("astromyllc@gmail.com")
				.fatherOccupation("Systems Engineer")
				.fatherContact1("0262057243")
				.fatherContact2("0554467612")
				.fatherPlaceOfWork("UENR")

				.motherEmail("mavisfas@gmail.com")
				.motherContact1("02003001964")
				.motherContact2("")
				.motherFirstNames("Mavis Fosua")
				.motherLastName("Asamoah")
				.motherOccupation("Caterer")
				.motherPlaceOfWork("Nsoatere")

				.applicantOtherName("Samuel")
				.applicantFirstName("Abel")
				.applicantLastName("Ackon")
				.applicantGender("Male")
				.applicantDenomination("Catholic")
				.applicantNationality("Ghana")
				.applicantPicture("drivec")
				.applicantCountryOfBirth("Ghana")
				.applicantPlaceOfBirth("Sunyani")
				.applicantDateOfBirth(LocalDate.parse("2015-09-05",formatter))
				.applicantBirthCert("DriveC")

				.applicationInstitution("06018007")

				.nameOfPreviousSchool("Blessed Assurance School")
				.addressOfPreviousSchool("Box 444 Sunyani")
				.classOfDeparture("Basic 5")
				.contactOfPreviousSchool("05466676876")
				.reasonForDeparture("Something something")
				.build();
	}


}
