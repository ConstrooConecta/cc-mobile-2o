package com.example.construconecta_interdisciplinar_certo.checkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.EnderecoApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityCadastroEnderecoBinding;
import com.example.construconecta_interdisciplinar_certo.models.Endereco;
import com.example.construconecta_interdisciplinar_certo.shop.conta.MeusEnderecosActivity;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroEnderecoActivity extends BaseActivity {
    private ActivityCadastroEnderecoBinding binding;
    private Map<String, String[]> cidadesPorEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Inicializa o mapa de cidades para cada estado
        cidadesPorEstado = new HashMap<>();
        cidadesPorEstado.put("AC", new String[]{"Acrelândia", "Assis Brasil", "Brasiléia", "Bujari", "Capixaba", "Cruzeiro do Sul", "Epitaciolândia", "Feijó", "Jordão", "Mâncio Lima", "Plácido de Castro", "Porto Acre", "Rio Branco", "Rodrigues Alves", "Santa Rosa do Purus", "Sena Madureira", "Senador Guiomard", "Tarauacá", "Xapuri"});
        cidadesPorEstado.put("AL", new String[]{"Arapiraca", "Maceió", "Palmeira dos Índios", "Rio Largo", "Satuba", "Viçosa"});
        cidadesPorEstado.put("AP", new String[]{"Amapa", "Amapá", "Ferreira Gomes", "Laranjal do Jari", "Macapá", "Mazagão", "Porto Grande", "Santana", "Tartarugalzinho", "Vitória do Jari"});
        cidadesPorEstado.put("AM", new String[]{"Alvarães", "Amaturá", "Anamã", "Apuí", "Atalaia do Norte", "Autazes", "Barcelos", "Benjamin Constant", "Beruri", "Boa Vista do Ramos", "Boca do Acre", "Borba", "Caapiranga", "Canutama", "Carauari", "Careiro", "Careiro da Várzea", "Coari", "Codajás", "Eirunepé", "Envira", "Fonte Boa", "Guajará", "Humaitá", "Ipixuna", "Itacoatiara", "Itamarati", "Japurá", "Jutaí", "Lábrea", "Manacapuru", "Manaquiri", "Manaus", "Manicoré", "Maraã", "Maués", "Novo Airão", "Novo Aripuanã", "Parintins", "Presidente Figueiredo", "Rio Preto da Eva", "Santa Isabel do Rio Negro", "Santo Antônio do Içá", "São Gabriel da Cachoeira", "São Paulo de Olivença", "Silves", "Tabatinga", "Tapauá", "Tefé", "Urucurituba"});
        cidadesPorEstado.put("BA", new String[]{"Amargosa", "Barreiras", "Camaçari", "Campo Formoso", "Candeias", "Caravelas", "Catu", "Conceição do Coité", "Conde", "Cruz das Almas", "Eunápolis", "Feira de Santana", "Guanambi", "Ilhéus", "Itabuna", "Itamaraju", "Itapetinga", "Jacobina", "Juazeiro", "Lauro de Freitas", "Morro do Chapéu", "Paulo Afonso", "Salvador", "Santa Maria da Vitória", "Santo Antônio de Jesus", "São Sebastião do Passé", "Teixeira de Freitas", "Vitória da Conquista"});
        cidadesPorEstado.put("CE", new String[]{"Abaiara", "Acarape", "Acaraú", "Aiuaba", "Alcântaras", "Altaneira", "Alto Santo", "Amontada", "Antonina do Norte", "Apuiarés", "Aquiraz", "Aracati", "Araripe", "Arneiroz", "Assaré", "Barbalha", "Barreira", "Baturité", "Beberibe", "Boa Viagem", "Brejo Santo", "Camocim", "Campos Sales", "Canindé", "Capistrano", "Caridade", "Caririaçu", "Cascavel", "Catarina", "Caucaia", "Cedro", "Choró", "Chorozinho", "Crateús", "Crato", "Eusébio", "Fortaleza", "Guaiúba", "Iguatu", "Independência", "Ipu", "Itaitinga", "Itapajé", "Itapipoca", "Itarema", "Jaguaretama", "Jardim", "Jati", "Juazeiro do Norte", "Lavras da Mangabeira", "Limoeiro do Norte", "Maracanaú", "Marco", "Massapê", "Morada Nova", "Morrinhos", "Mucambo", "Ocara", "Palhano", "Pentecoste", "Quixeramobim", "Quixadá", "Quixilins", "Redenção", "Russas", "Saboeiro", "Salitre", "Santana do Acaraú", "São Benedito", "São Gonçalo do Amarante", "São João do Jaguaribe", "Senador Pompeu", "Sobral", "Solonópole", "Tabuleiro do Norte", "Tarrafas", "Tauá", "Tejuçuoca", "Tianguá", "Trairi", "Ubajara", "Umari", "Uruoca", "Varjota", "Viçosa do Ceará"});
        cidadesPorEstado.put("DF", new String[]{"Brazlândia", "Ceilândia", "Guará", "Lago Norte", "Lago Sul", "Planaltina", "Plano Piloto", "Samambaia", "Santa Maria", "São Sebastião", "Taguatinga", "Recanto das Emas"});
        cidadesPorEstado.put("ES", new String[]{"Anchieta", "Alegre", "Alfredo Chaves", "Baixo Guandu", "Barra de São Francisco", "Cachoeiro de Itapemirim", "Domingos Martins", "Guarapari", "Linhares", "Montanha", "Muqui", "Nova Venécia", "Rio Novo do Sul", "São Mateus", "Serra", "Viana", "Vitória"});
        cidadesPorEstado.put("GO", new String[]{"Água Fria de Goiás", "Alexânia", "Anápolis", "Aporé", "Apore", "Araguapaz", "Aruanã", "Caldas Novas", "Ceres", "Cristalina", "Goiânia", "Goianésia", "Goiatuba", "Inhumas", "Itaberaí", "Itauçu", "Jataí", "Luziania", "Morrinhos", "Nerópolis", "Nova Glória", "Rio Verde", "Senador Canedo", "Trindade", "Valparaíso de Goiás", "Vianópolis"});
        cidadesPorEstado.put("MA", new String[]{"Açailândia", "Afonso Cunha", "Alcântara", "Aldeias Altas", "Anajatuba", "Anapurus", "Araioses", "Arame", "Arari", "Axixá", "Barra do Corda", "Bacabal", "Bacabeira", "Caxias", "Codó", "Davinópolis", "Entroncamento", "Estreito", "Grajaú", "Imperatriz", "Itinga do Maranhão", "Paço do Lumiar", "Pedreiras", "Pinheiro", "Presidente Dutra", "Rosário", "Santa Inês", "São Luís", "São José de Ribamar", "Timon", "Tutóia", "Vargem Grande"});
        cidadesPorEstado.put("MT", new String[]{"Água Boa", "Barra do Garças", "Cáceres", "Campo Verde", "Canarana", "Chapada dos Guimarães", "Cláudia", "Colíder", "Confresa", "Cuiabá", "Diamantino", "Guarantã do Norte", "Jaciara", "Juína", "Lucas do Rio Verde", "Marcelândia", "Matupá", "Mirassol d'Oeste", "Nobres", "Pontes e Lacerda", "Rondonópolis", "Sorriso", "Tangará da Serra", "Várzea Grande"});
        cidadesPorEstado.put("MS", new String[]{"Água Clara", "Anaurilândia", "Aparecida do Taboado", "Bataguassu", "Bela Vista", "Campo Grande", "Coxim", "Dourados", "Eldorado", "Iguatemi", "Itaporã", "Jardim", "Maracaju", "Mundo Novo", "Naviraí", "Nova Andradina", "Paranaíba", "Paranhos", "Ponta Porã", "Rio Brilhante", "Rio Verde de Mato Grosso", "Selvíria", "Três Lagoas", "Vicentina"});
        cidadesPorEstado.put("MG", new String[]{"Alfenas", "Belo Horizonte", "Contagem", "Divinópolis", "Juiz de Fora", "Montes Claros", "Oliveira", "Passos", "Poços de Caldas", "Pouso Alegre", "Sete Lagoas", "Uberaba", "Uberlândia", "Varginha"});
        cidadesPorEstado.put("PA", new String[]{"Abaetetuba", "Afuá", "Alenquer", "Altamira", "Ananindeua", "Angra dos Reis", "Barcarena", "Belém", "Benevides", "Bragança", "Breves", "Castanhal", "Curuçá", "Eldorado dos Carajás", "Marabá", "Paragominas", "Parauapebas", "Redenção", "Santarém", "Tailândia", "Tomé-Açu", "Tracuateua"});
        cidadesPorEstado.put("PB", new String[]{"Alagoa Grande", "Alagoa Nova", "Aparecida", "Areia", "Bananeiras", "Belém", "Campina Grande", "Catolé do Rocha", "Conde", "Cruz do Espírito Santo", "Guarabira", "João Pessoa", "Maturéia", "Patos", "Santa Rita", "Sousa", "Sumé", "Teixeira", "Vieirópolis"});
        cidadesPorEstado.put("PR", new String[]{"Apucarana", "Campo Largo", "Cascavel", "Curitiba", "Foz do Iguaçu", "Londrina", "Maringá", "Ponta Grossa", "São José dos Pinhais", "Toledo"});
        cidadesPorEstado.put("PE", new String[]{"Afogados da Ingazeira", "Arcoverde", "Barreiros", "Caruaru", "Garanhuns", "Ipojuca", "Olinda", "Recife", "Salgueiro", "São Lourenço da Mata", "Surubim", "Timbaúba"});
        cidadesPorEstado.put("PI", new String[]{"Acauã", "Agricolândia", "Alagoinha do Piauí", "Amarante", "Angical do Piauí", "Bertolínia", "Bocaina", "Campo Maior", "Cocal", "Codo", "Floriano", "Jaicós", "Parnaíba", "Picos", "Piripiri", "São João do Piauí", "Teresina", "Uruçuí"});
        cidadesPorEstado.put("RJ", new String[]{"Angra dos Reis", "Belford Roxo", "Campos dos Goytacazes", "Duque de Caxias", "Niterói", "Petrópolis", "Rio de Janeiro", "São Gonçalo", "Volta Redonda"});
        cidadesPorEstado.put("RN", new String[]{"Acari", "Afonso Bezerra", "Areia Branca", "Caicó", "Currais Novos", "Fernando Pedroza", "Mossoró", "Natal", "Pau dos Ferros", "São Gonçalo do Amarante", "Santa Cruz"});
        cidadesPorEstado.put("RS", new String[]{"Bagé", "Canoas", "Caxias do Sul", "Pelotas", "Porto Alegre", "Rio Grande", "Santa Maria", "Santa Rosa", "São Leopoldo", "Torres"});
        cidadesPorEstado.put("RO", new String[]{"Alta Floresta d'Oeste", "Ariquemes", "Buritis", "Cacoal", "Jaru", "Ji-Paraná", "Porto Velho", "Rolim de Moura", "Vilhena"});
        cidadesPorEstado.put("RR", new String[]{"Alto Alegre", "Amajari", "Boa Vista", "Bonfim", "Caracaraí", "Iracema", "Mucajaí", "Rorainópolis", "São João da Baliza"});
        cidadesPorEstado.put("SC", new String[]{"Blumenau", "Florianópolis", "Joinville", "Lages", "São José", "Tubarão"});
        cidadesPorEstado.put("SP", new String[]{"Adamantina", "Araçatuba", "Araraquara", "Bauru", "Campinas", "Guarulhos", "São Bernardo do Campo", "São Paulo", "Santos", "Sorocaba"});
        cidadesPorEstado.put("SE", new String[]{"Aracaju", "Estância", "Lagarto", "Nossa Senhora do Socorro", "Propriá", "São Cristóvão", "Tobias Barreto"});
        cidadesPorEstado.put("TO", new String[]{"Palmas", "Araguaína", "Gurupi", "Porto Nacional", "Tocantinópolis"});

        String[] estados = new String[]{"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                R.id.dropdownText,
                estados
        );
        binding.estadoInput.setAdapter(adapter);
        binding.estadoInput.setOnClickListener(v -> binding.estadoInput.showDropDown());

        // Desativa o campo de cidade no início
        binding.cidadeInput.setEnabled(false);
        binding.cidadeInput.setTextColor(Color.GRAY);

        // Configura a ação ao selecionar um estado
        binding.estadoInput.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String estadoSelecionado = (String) parent.getItemAtPosition(position);

            // Habilita o campo de cidade e atualiza sua cor
            binding.cidadeInput.setEnabled(true);
            binding.cidadeInput.setTextColor(Color.BLACK);

            // Configura as cidades correspondentes ao estado selecionado
            String[] cidades = cidadesPorEstado.getOrDefault(estadoSelecionado, new String[]{});
            ArrayAdapter<String> cidadeAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item, R.id.dropdownText, cidades);
            binding.cidadeInput.setAdapter(cidadeAdapter);
            binding.cidadeInput.setOnClickListener(v -> binding.cidadeInput.showDropDown());
        });
        binding.btnAdicionarEndereco.setOnClickListener(v -> cadastrarEndereco());

        binding.imageButton15.setOnClickListener(v -> finish());

        binding.cepInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validarCEP(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.bairroInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validarBairro(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.ruaInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validarRua(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.numeroInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validarNumero(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.complementoInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validarComplemento(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void cadastrarEndereco() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        assert usuarioAtual != null;

        // Coleta os dados dos campos
        String cep = binding.cepInput.getText().toString();
        String uf = binding.estadoInput.getText().toString();
        String cidade = binding.cidadeInput.getText().toString();
        String bairro = binding.bairroInput.getText().toString();
        String rua = binding.ruaInput.getText().toString();
        String numero = binding.numeroInput.getText().toString();
        String complemento = binding.complementoInput.getText().toString();

        // Cria uma instância do objeto Endereco
        Endereco endereco = new Endereco(
                0, // ID pode ser 0 se for gerado pelo banco de dados
                cep,
                uf,
                cidade,
                bairro,
                rua,
                numero,
                complemento,
                usuarioAtual.getUid()
        );

        // Adiciona o endereço ao banco de dados
        addAddresToDatabase(endereco);
    }

    public void addAddresToDatabase(Endereco endereco) {
        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EnderecoApi api = retrofit.create(EnderecoApi.class);
        Call<Endereco> call = api.createAddress(endereco);

        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Double total = getIntent().getExtras() != null ? getIntent().getExtras().getDouble("total") : null;
                    if (total != null) {
                        String canal = getIntent().getExtras() != null ? getIntent().getExtras().getString("canal") : null;
                        if (canal.equals("meusEnderecos")) {
                            Intent intent = new Intent(CadastroEnderecoActivity.this, MeusEnderecosActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(CadastroEnderecoActivity.this, EscolhaEnderecoActivity.class);
                            intent.putExtra("total", total);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(CadastroEnderecoActivity.this, "Erro ao cadastrar endereço: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable throwable) {
                Toast.makeText(CadastroEnderecoActivity.this, "Falha na conexão: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void validarCEP(String cep) {
        if (cep.isEmpty()) {
            binding.cepInput.setError("O CEP é obrigatório");
        } else if (cep.length() != 8) {
            binding.cepInput.setError("O CEP deve ter 8 caracteres");
        } else {
            binding.cepInput.setError(null); // Limpa o erro=
        }
    }

    private void validarBairro(String bairro) {
        if (bairro.isEmpty()) {
            binding.bairroInput.setError("O bairro é obrigatório");
        } else if (bairro.length() < 1 || bairro.length() > 53) {
            binding.bairroInput.setError("O bairro deve ter no mínimo 1 e no máximo 53 caracteres");
        } else {
            binding.bairroInput.setError(null); // Limpa o erro
        }
    }

    private void validarRua(String rua) {
        if (rua.isEmpty()) {
            binding.ruaInput.setError("A rua é obrigatória");
        } else if (rua.length() > 75) {
            binding.ruaInput.setError("A rua deve ter no máximo 75 caracteres");
        } else {
            binding.ruaInput.setError(null); // Limpa o erro
        }
    }

    private void validarNumero(String numero) {
        if (numero.length() > 20) {
            binding.numeroInput.setError("O número deve ter no máximo 20 caracteres");
        } else {
            binding.numeroInput.setError(null); // Limpa o erro
        }
    }

    private void validarComplemento(String complemento) {
        if (complemento.length() > 150) {
            binding.complementoInput.setError("O complemento deve ter no máximo 150 caracteres");
        } else {
            binding.complementoInput.setError(null); // Limpa o erro
        }
    }
}
