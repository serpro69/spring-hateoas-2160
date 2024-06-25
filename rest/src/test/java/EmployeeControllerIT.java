import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import payroll.Employee;
import payroll.PayrollApplication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = PayrollApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIT {

    @Value(value = "${local.server.port}")
    private int port;

    private TestRestTemplate rest;

    @BeforeEach
    public void setup() {
        rest = new TestRestTemplate(new RestTemplateBuilder().rootUri("http://localhost:" + port));
    }

    @Test
    public void testGettingAllEmployeesWithAcceptHalFormsHeader() {
        final var headers = new HttpHeaders();
        headers.add("Accept", MediaTypes.HAL_FORMS_JSON_VALUE);
        var httpEntity = new HttpEntity<>(headers);
        var response = rest.exchange(
                "/employees",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<CollectionModel<EntityModel<Employee>>>() {});
        var resources = response.getBody();
        assertThat(resources.getContent()).isNotNull();
        var employees = resources.getContent();
        System.out.println(resources);
        assertThat(employees).isNotNull();
        assertThat(employees.size()).isEqualTo(2);
    }

    @Test
    public void testGettingAllEmployeesWithoutAcceptHalFormsHeader() {
        var response = rest.exchange(
                "/employees",
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                new ParameterizedTypeReference<CollectionModel<EntityModel<Employee>>>() {});
        var resources = response.getBody();
        assertThat(resources.getContent()).isNotNull();
        var employees = resources.getContent();
        // NB! resources returns empty content in collection model
        System.out.println(resources);
        assertThat(employees).isNotNull();
        assertThat(employees.size()).isEqualTo(2);
    }

    @Test
    public void testGettingAllEmployeesWithStringResponse() {
        var response = rest.exchange(
                "/employees",
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                String.class); // Deserialize to String to check that body is not empty
        var resources = response.getBody();
        assertThat(resources).isNotNull();
        // NB! when deserializing to String, the response body is not empty
        System.out.println(resources);
    }
}
