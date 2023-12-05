package grafos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class Aresta {
    int vertice;
    int peso;

    public Aresta(int destino, int peso) {
        this.vertice = destino;
        this.peso = peso;
    }
}

class Usuario {
    int id;
    String nome;
    Set<String> interesses;

    public Usuario(int id, String nome, Set<String> interesses) {
        this.id = id;
        this.nome = nome;
        this.interesses = interesses;
    }
}

class RedeSocial {
    private Map<Usuario, List<Aresta>> grafo;

    public RedeSocial() {
        this.grafo = new HashMap<>();
    }

    public void adicionarUsuario(Usuario usuario) {
        if (!grafo.containsKey(usuario)) {
            grafo.put(usuario, new LinkedList<>());
        }
    }

    public void adicionarConexao(Usuario usuario1, Usuario usuario2, int peso) {
        if (!grafo.containsKey(usuario1) || !grafo.containsKey(usuario2)) {
            throw new IllegalArgumentException("Os usuários devem existir na rede.");
        }

        if (usuario2 != null) {
            grafo.get(usuario1).add(new Aresta(usuario2.id, peso));
        }

        if (usuario1 != null) {
            grafo.get(usuario2).add(new Aresta(usuario1.id, peso));
        }
    }

    public void removerConexao(Usuario usuario1, Usuario usuario2) {
        if (!grafo.containsKey(usuario1) || !grafo.containsKey(usuario2)) {
            throw new IllegalArgumentException("Os usuários devem existir na rede.");
        }

        grafo.get(usuario1).removeIf(a -> a.vertice == usuario2.id);
        grafo.get(usuario2).removeIf(a -> a.vertice == usuario1.id);
    }

    public void listarContatos(Usuario usuario) {
        System.out.println("Contatos de " + usuario.nome + ":");
        List<Aresta> conexoes = grafo.get(usuario);
        for (Aresta conexao : conexoes) {
            Usuario contato = obterUsuarioPorId(conexao.vertice);
            System.out.println("Nome: " + contato.nome + ", Interesses: " + contato.interesses);
        }
    }

    public boolean verificarAlcance(Usuario origem, Usuario destino) {
        Set<Usuario> visitados = new HashSet<>();
        return existeCaminho(origem, destino, visitados);
    }

    private boolean existeCaminho(Usuario usuarioAtual, Usuario destino, Set<Usuario> visitados) {
        if (usuarioAtual.equals(destino)) {
            System.out.println("Caminho encontrado!");
            return true;
        }

        visitados.add(usuarioAtual);
        List<Aresta> conexoes = grafo.get(usuarioAtual);
        if (conexoes != null) {
            for (Aresta conexao : conexoes) {
                Usuario adjacente = obterUsuarioPorId(conexao.vertice);
                if (!visitados.contains(adjacente) && existeCaminho(adjacente, destino, visitados)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void imprimirRedeSocial() {
    for (Map.Entry<Usuario, List<Aresta>> entry : grafo.entrySet()) {
        Usuario usuario = entry.getKey();
        List<Aresta> conexoes = entry.getValue();

        System.out.print("ID: " + usuario.id + ", Nome: " + usuario.nome + ", Interesses: " + usuario.interesses);
        
        if (!conexoes.isEmpty()) {
            System.out.print(" -> Conexões: ");
            for (Aresta conexao : conexoes) {
                Usuario contato = obterUsuarioPorId(conexao.vertice);
                System.out.print("ID: " + contato.id + ", Nome: " + contato.nome + " ");
            }
        }
        
        System.out.println();
    }
}

    public void salvarRedeSocial(String fileName) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
        for (Map.Entry<Usuario, List<Aresta>> entry : grafo.entrySet()) {
            Usuario usuario = entry.getKey();

            // Agora, salvar as informações do usuário no arquivo com espaços
            writer.println(usuario.id + ", " + usuario.nome + ", " + String.join(", ", usuario.interesses));
        }
        System.out.println("Rede Social salva com sucesso.");
    } catch (IOException e) {
        System.err.println("Erro ao salvar a Rede Social: " + e.getMessage());
    }
}
    
public void carregarRedeSocial(String fileName) {
    try (Scanner fileScanner = new Scanner(new File(fileName))) {
        while (fileScanner.hasNextLine()) {
            try {
                String linha = fileScanner.nextLine();
                String[] partes = linha.split(", ");

                if (partes.length == 3) {
                    int id = Integer.parseInt(partes[0]);
                    String nome = partes[1];
                    String[] interessesArray = partes[2].split(","); // Interesses separados por vírgula
                    Set<String> interesses = new HashSet<>(Arrays.asList(interessesArray));

                    Usuario usuario = new Usuario(id, nome, interesses);
                    adicionarUsuario(usuario);
                } else if (partes.length == 4) {
                    int id1 = Integer.parseInt(partes[0]);
                    int id2 = Integer.parseInt(partes[1]);
                    int peso = Integer.parseInt(partes[2]);

                    Usuario usuario1 = obterUsuarioPorId(id1);
                    Usuario usuario2 = obterUsuarioPorId(id2);

                    if (usuario1 != null && usuario2 != null) {
                        adicionarConexao(usuario1, usuario2, peso);
                    }
                } else {
                    System.err.println("Erro ao ler valores do arquivo. Linha com erro: " + linha);
                }
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter número. Linha com erro: " + fileScanner.nextLine());
            }
        }
        System.out.println("Rede Social carregada com sucesso.");
    } catch (FileNotFoundException e) {
        System.err.println("Arquivo não encontrado: " + e.getMessage());
    }
}

    Usuario obterUsuarioPorId(int id) {
        for (Usuario usuario : grafo.keySet()) {
            if (usuario.id == id) {
                return usuario;
            }
        }
        return null;
    }
}

public class Grafos {
    public static int menuMetodos() {
        Scanner scanner = new Scanner(System.in);
        int op;
        System.out.println("1-Adicionar Usuário");
        System.out.println("2-Adicionar Conexão");
        System.out.println("3-Listar Contatos");
        System.out.println("4-Verificar Alcance");
        System.out.println("5-Imprimir Rede Social");
        System.out.println("6-Salvar Rede Social");
        System.out.println("7-Carregar Rede Social");
        System.out.println("8-Remover Conexão");
        System.out.println("0-Sair");
        op = scanner.nextInt();
        return op;
    }


    public static void main(String[] args) throws FileNotFoundException {
        RedeSocial redeSocial = new RedeSocial();
        Scanner scanner = new Scanner(System.in);
        int op = 0;

        do {
            op = menuMetodos();
            switch (op) {
                case 1: {
                    System.out.println("ID:");
                    int id = scanner.nextInt();
                    System.out.println("Nome:");
                    String nome = scanner.next();
                    System.out.println("Número de Interesses:");
                    int numInteresses = scanner.nextInt();
                    Set<String> interesses = new HashSet<>();
                    scanner.nextLine(); // Consumir a quebra de linha após o nextInt
                    for (int i = 0; i < numInteresses; i++) {
                        System.out.println("Interesse " + (i + 1) + ":");
                        interesses.add(scanner.nextLine());
                    }
                    Usuario usuario = new Usuario(id, nome, interesses);
                    redeSocial.adicionarUsuario(usuario);
                    break;
                }
                case 2: {
                    System.out.println("ID do Usuário 1:");
                    int idUsuario1 = scanner.nextInt();
                    Usuario usuario1 = redeSocial.obterUsuarioPorId(idUsuario1);
                    System.out.println("ID do Usuário 2:");
                    int idUsuario2 = scanner.nextInt();
                    Usuario usuario2 = redeSocial.obterUsuarioPorId(idUsuario2);
                    System.out.println("Peso da Conexão:");
                    int pesoConexao = scanner.nextInt();
                    redeSocial.adicionarConexao(usuario1, usuario2, pesoConexao);
                    break;
                }
                case 3: {
                    System.out.println("ID do Usuário:");
                    int idContatos = scanner.nextInt();
                    Usuario usuarioContatos = redeSocial.obterUsuarioPorId(idContatos);
                    redeSocial.listarContatos(usuarioContatos);
                    break;
                }
                case 4: {
                    System.out.println("ID do Usuário de Origem:");
                    int idOrigem = scanner.nextInt();
                    Usuario usuarioOrigem = redeSocial.obterUsuarioPorId(idOrigem);
                    System.out.println("ID do Usuário de Destino:");
                    int idDestino = scanner.nextInt();
                    Usuario usuarioDestino = redeSocial.obterUsuarioPorId(idDestino);
                    boolean alcance = redeSocial.verificarAlcance(usuarioOrigem, usuarioDestino);
                    System.out.println("Alcance entre os usuários: " + alcance);
                    break;
                }
                case 5:
                    redeSocial.imprimirRedeSocial();
                    break;
                case 6: {
                System.out.println("Digite o nome do arquivo para salvar:");
                String fileName = scanner.next();
                redeSocial.salvarRedeSocial(fileName);
                break;
            }
            case 7: {
                System.out.println("Digite o nome do arquivo para carregar:");
                String fileName = scanner.next();
                redeSocial.carregarRedeSocial(fileName);
                break;
            }
            case 8: {
                System.out.println("ID do Usuário 1:");
                int idUsuario1 = scanner.nextInt();
                Usuario usuario1 = redeSocial.obterUsuarioPorId(idUsuario1);
                System.out.println("ID do Usuário 2:");
                int idUsuario2 = scanner.nextInt();
                Usuario usuario2 = redeSocial.obterUsuarioPorId(idUsuario2);
                redeSocial.removerConexao(usuario1, usuario2);
                break;
}
            case 0: {
                System.out.println("Saindo");
                redeSocial.salvarRedeSocial("rede_social_salva.txt");
                break;
            }
            default:
                System.out.println("Opção inválida");
        }
    } while (op != 0);
}
    
}