1|package hei.school.library.service.customer;
2|
3|import static org.assertj.core.api.Assertions.assertThat;
4|import static org.assertj.core.api.Assertions.assertThatThrownBy;
5|import static org.mockito.ArgumentMatchers.any;
6|import static org.mockito.ArgumentMatchers.eq;
7|import static org.mockito.ArgumentMatchers.isNull;
8|import static org.mockito.Mockito.verify;
9|import static org.mockito.Mockito.when;
10|
11|import hei.school.library.dto.CustomerResponse;
12|import hei.school.library.dto.CustomerUpdateRequest;
13|import hei.school.library.entity.Customer;
14|import hei.school.library.exception.NotFoundException;
15|import hei.school.library.exception.UnprocessableEntityException;
16|import hei.school.library.mapper.CustomerMapper;
17|import hei.school.library.mapper.PaginationMapper;
18|import hei.school.library.repository.dao.CustomerRepository;
19|import hei.school.library.service.CustomerService;
20|import hei.school.library.validator.DataValidator;
21|import java.time.Instant;
22|import java.time.LocalDate;
23|import java.util.Optional;
24|import java.util.UUID;
25|import org.junit.jupiter.api.BeforeEach;
26|import org.junit.jupiter.api.DisplayName;
27|import org.junit.jupiter.api.Test;
28|import org.junit.jupiter.api.extension.ExtendWith;
29|import org.mockito.Mock;
30|import org.mockito.junit.jupiter.MockitoExtension;
31|
32|@ExtendWith(MockitoExtension.class)
33|class PatchCustomersServiceTest {
34|
35|  @Mock private CustomerRepository customerRepository;
36|
37|  private CustomerService customerService;
38|
39|  private UUID existingId;
40|  private Instant now;
41|
42|  @BeforeEach
43|  void setUp() {
44|    customerService =
45|        new CustomerService(
46|            customerRepository, new CustomerMapper(new PaginationMapper()), new DataValidator());
47|
48|    existingId = UUID.randomUUID();
49|    now = Instant.now();
50|  }
51|
52|  private static Customer customer(UUID id, String lastName, String firstName,
53|      LocalDate birthDate, String email, String phone, Instant now) {
54|    return new Customer(id, lastName, firstName, birthDate, email, phone, now, now);
55|  }
56|
57|  @Test
58|  @DisplayName("update: should update firstName only and return DTO")
59|  void update_shouldUpdateFirstNameOnly() {
60|    CustomerUpdateRequest request =
61|        new CustomerUpdateRequest(null, "Marie Claire", null, null, null);
62|
63|    Customer updated =
64|        customer(existingId, "Dupont", "Marie Claire", LocalDate.of(1995, 3, 10),
65|            "marie@mail.com", "+261****4567", now);
66|    when(customerRepository.patch(
67|            eq(existingId), isNull(), eq("Marie Claire"), isNull(), isNull(), isNull()))
68|        .thenReturn(Optional.of(updated));
69|
70|    CustomerResponse result = customerService.update(existingId, request);
71|
72|    assertThat(result.getFirstName()).isEqualTo("Marie Claire");
73|    assertThat(result.getLastName()).isEqualTo("Dupont");
74|    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
75|    verify(customerRepository)
76|        .patch(eq(existingId), isNull(), eq("Marie Claire"), isNull(), isNull(), isNull());
77|  }
78|
79|  @Test
80|  @DisplayName("update: should update lastName only and return DTO")
81|  void update_shouldUpdateLastNameOnly() {
82|    CustomerUpdateRequest request = new CustomerUpdateRequest("Martin", null, null, null, null);
83|
84|    Customer updated =
85|        customer(existingId, "Martin", "Marie", LocalDate.of(1995, 3, 10),
86|            "marie@mail.com", "+261****4567", now);
87|    when(customerRepository.patch(
88|            eq(existingId), eq("Martin"), isNull(), isNull(), isNull(), isNull()))
89|        .thenReturn(Optional.of(updated));
90|
91|    CustomerResponse result = customerService.update(existingId, request);
92|
93|    assertThat(result.getLastName()).isEqualTo("Martin");
94|    assertThat(result.getFirstName()).isEqualTo("Marie");
95|    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
96|  }
97|
98|  @Test
99|  @DisplayName("update: should update email only and return DTO")
100|  void update_shouldUpdateEmailOnly() {
101|    CustomerUpdateRequest request =
102|        new CustomerUpdateRequest(null, null, null, "new@mail.com", null);
103|
104|    Customer updated =
105|        customer(existingId, "Dupont", "Marie", LocalDate.of(1995, 3, 10),
106|            "new@mail.com", "+261****4567", now);
107|    when(customerRepository.patch(
108|            eq(existingId), isNull(), isNull(), isNull(), eq("new@mail.com"), isNull()))
109|        .thenReturn(Optional.of(updated));
110|
111|    CustomerResponse result = customerService.update(existingId, request);
112|
113|    assertThat(result.getEmail()).isEqualTo("new@mail.com");
114|    assertThat(result.getLastName()).isEqualTo("Dupont");
115|  }
116|
117|  @Test
118|  @DisplayName("update: should update phone only and return DTO")
119|  void update_shouldUpdatePhoneOnly() {
120|    CustomerUpdateRequest request =
121|        new CustomerUpdateRequest(null, null, null, null, "+261****4000");
122|
123|    Customer updated =
124|        customer(existingId, "Dupont", "Marie", LocalDate.of(1995, 3, 10),
125|            "marie@mail.com", "+261****4000", now);
126|    when(customerRepository.patch(
127|            eq(existingId), isNull(), isNull(), isNull(), isNull(), eq("+261****4000")))
128|        .thenReturn(Optional.of(updated));
129|
130|    CustomerResponse result = customerService.update(existingId, request);
131|
132|    assertThat(result.getPhone()).isEqualTo("+261****4000");
133|    assertThat(result.getLastName()).isEqualTo("Dupont");
134|  }
135|
136|  @Test
137|  @DisplayName("update: should update birthDate only and return DTO")
138|  void update_shouldUpdateBirthDateOnly() {
139|    LocalDate newBirthDate = LocalDate.of(1990, 7, 15);
140|    CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, newBirthDate, null, null);
141|
142|    Customer updated =
143|        customer(existingId, "Dupont", "Marie", newBirthDate,
144|            "marie@mail.com", "+261****4567", now);
145|    when(customerRepository.patch(
146|            eq(existingId), isNull(), isNull(), eq(newBirthDate), isNull(), isNull()))
147|        .thenReturn(Optional.of(updated));
148|
149|    CustomerResponse result = customerService.update(existingId, request);
150|
151|    assertThat(result.getBirthDate()).isEqualTo(newBirthDate);
152|  }
153|
154|  @Test
155|  @DisplayName("update: should update all fields at once")
156|  void update_shouldUpdateAllFields() {
157|    CustomerUpdateRequest request =
158|        new CustomerUpdateRequest(
159|            "Martin", "Jean", LocalDate.of(1988, 1, 1), "jean@mail.com", "+261****4999");
160|
161|    Customer updated =
162|        customer(existingId, "Martin", "Jean", LocalDate.of(1988, 1, 1),
163|            "jean@mail.com", "+261****4999", now);
164|    when(customerRepository.patch(
165|            eq(existingId),
166|            eq("Martin"),
167|            eq("Jean"),
168|            eq(LocalDate.of(1988, 1, 1)),
169|            eq("jean@mail.com"),
170|            eq("+261****4999")))
171|        .thenReturn(Optional.of(updated));
172|
173|    CustomerResponse result = customerService.update(existingId, request);
174|
175|    assertThat(result.getLastName()).isEqualTo("Martin");
176|    assertThat(result.getFirstName()).isEqualTo("Jean");
177|    assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1988, 1, 1));
178|    assertThat(result.getEmail()).isEqualTo("jean@mail.com");
179|    assertThat(result.getPhone()).isEqualTo("+261****4999");
180|  }
181|
182|  @Test
183|  @DisplayName("update: should throw NotFoundException when customer not found")
184|  void update_shouldThrow_whenNotFound() {
185|    UUID unknownId = UUID.randomUUID();
186|    when(customerRepository.patch(
187|            eq(unknownId), eq("test"), isNull(), isNull(), isNull(), isNull()))
188|        .thenReturn(Optional.empty());
189|
190|    assertThatThrownBy(
191|            () ->
192|                customerService.update(
193|                    unknownId, new CustomerUpdateRequest("test", null, null, null, null)))
194|        .isInstanceOf(NotFoundException.class);
195|
196|    verify(customerRepository)
197|        .patch(eq(unknownId), eq("test"), isNull(), isNull(), isNull(), isNull());
198|  }
199|
200|  @Test
201|  @DisplayName("update: should throw UnprocessableEntityException when all fields are null")
202|  void update_shouldThrow_whenAllFieldsNull() {
203|    CustomerUpdateRequest emptyRequest = new CustomerUpdateRequest();
204|
205|    assertThatThrownBy(() -> customerService.update(existingId, emptyRequest))
206|        .isInstanceOf(UnprocessableEntityException.class)
207|        .hasMessage("At least one field is required.");
208|  }
209|
210|  @Test
211|  @DisplayName("update: should throw UnprocessableEntityException when birthDate is in the future")
212|  void update_shouldThrow_whenBirthDateInFuture() {
213|    CustomerUpdateRequest request =
214|        new CustomerUpdateRequest(null, null, LocalDate.now().plusDays(1), null, null);
215|
216|    assertThatThrownBy(() -> customerService.update(existingId, request))
217|        .isInstanceOf(UnprocessableEntityException.class)
218|        .hasMessage("birthDate cannot be in the future.");
219|  }
220|
221|  @Test
222|  @DisplayName(
223|      "update: should throw UnprocessableEntityException when lastName contains invalid characters")
224|  void update_shouldThrow_whenLastNameInvalid() {
225|    CustomerUpdateRequest request = new CustomerUpdateRequest("Dupont123", null, null, null, null);
226|
227|    assertThatThrownBy(() -> customerService.update(existingId, request))
228|        .isInstanceOf(UnprocessableEntityException.class)
229|        .hasMessageContaining("forbidden characters");
230|  }
231|
232|  @Test
233|  @DisplayName("update: should throw UnprocessableEntityException when email has invalid format")
234|  void update_shouldThrow_whenEmailInvalid() {
235|    CustomerUpdateRequest request =
236|        new CustomerUpdateRequest(null, null, null, "not-an-email", null);
237|
238|    assertThatThrownBy(() -> customerService.update(existingId, request))
239|        .isInstanceOf(UnprocessableEntityException.class)
240|        .hasMessageContaining("Invalid email format");
241|  }
242|
243|  @Test
244|  @DisplayName("update: should throw UnprocessableEntityException when phone has invalid format")
245|  void update_shouldThrow_whenPhoneInvalid() {
246|    CustomerUpdateRequest request =
247|        new CustomerUpdateRequest(null, null, null, null, "not-a-phone");
248|
249|    assertThatThrownBy(() -> customerService.update(existingId, request))
250|        .isInstanceOf(UnprocessableEntityException.class)
251|        .hasMessageContaining("Invalid phone format");
252|  }
253|}
254|