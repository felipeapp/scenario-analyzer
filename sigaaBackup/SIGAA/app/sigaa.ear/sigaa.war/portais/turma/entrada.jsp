<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>

<h:form>
<div class="descricaoOperacao">
<p>Caro Usuário,</p>
<p>&nbsp;</p>
<p>A Turma Virtual é uma ferramenta que possibilita a utilização de recursos de informática no ensino presencial. Através desta ferramenta é possível: </p>

<ul>
	<li> Disponibilizar arquivos (apresentações, textos, etc) para os alunos; </li>
	<li> Utilizar um porta-arquivos para armazenar seus arquivos; </li>
	<li> Lançar a freqüência dos alunos de forma integrada com o Diário de Classe; </li>
	<li> Colocar um cronograma de aulas informando o conteúdo que será abordado; </li>
	<li> Visualizar os participantes da turma: todos os participantes da turma poderão visualizar uns aos outros, sejam discentes ou docentes; </li>
	<li> Imprimir o diário de classe: docente poderá imprimir o diário de classe da turma; </li>
	<li> Cadastrar avaliações: docente poderá agendar avaliações da turma, as quais serão visualizadas pelos discentes matriculados na turma;</li>
	<li> Editar informações da turma: docente poderá editar o plano de trabalho do componente curricular da turma, o qual será visualizado pelos discentes da turma. Nesta opção, o docente também poderá cadastrar as permissões dos alunos no portal da turma: criação de fóruns, criação de enquetes, visualização de notas de todos os alunos da turma, etc.</li>
	<li> Cadastrar e consolidar notas da turma: o docente poderá cadastrar as notas das avaliações que ele cadastrou, bem como consolidá-las através do portal da turma;</li>
	<li> Cadastrar notícias da turma: o docente poderá cadastrar notícias referentes à turma que está ministrando, elas serão visualizadas pelos alunos dessa turma;
	<li> Cadastrar Mural: o mural é um fórum criado para postagens de discussões específicas do componente da turma. O discente poderá visualizar e responder tais questões;</li>
	<li> Adicionar permissão: permite que o docente da turma dê permissão especial a um outro docente, aluno de mestrado, etc, a fim de que este possa, por exemplo, anexar material referente à aula ministrada para a turma;</li>
	<li> Inserir referências bibliográficas: docente poderá inserir referências bibliográficas inerentes ao componente ministrado, que serão visualizadas pelos discentes matriculados;</li>
	<li> Cadastrar fórum: O docente e os alunos da turma, desde que tenha sido dada permissão pelo docente, poderão cadastrar fóruns para discussão.</li>
	<li> Cadastrar enquete: : O docente e os alunos da turma, desde que tenha sido dada permissão pelo docente, poderão cadastrar e enquetes.</li>
</ul>
</div>

<c:if test="${ acesso.docente || portalTurma.permissao.docente }">
<big><center>
<h:commandLink value="Clique aqui" action="#{ portalTurma.atualizar }">
	<f:param name="expanded" value="true"/>
</h:commandLink> para alterar o plano de ensino.</center></big>
</c:if> 
</h:form>
</f:view>

<%@include file="/portais/turma/rodape.jsp"%>