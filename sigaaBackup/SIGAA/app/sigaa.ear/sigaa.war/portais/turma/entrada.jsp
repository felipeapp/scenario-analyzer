<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>

<h:form>
<div class="descricaoOperacao">
<p>Caro Usu�rio,</p>
<p>&nbsp;</p>
<p>A Turma Virtual � uma ferramenta que possibilita a utiliza��o de recursos de inform�tica no ensino presencial. Atrav�s desta ferramenta � poss�vel: </p>

<ul>
	<li> Disponibilizar arquivos (apresenta��es, textos, etc) para os alunos; </li>
	<li> Utilizar um porta-arquivos para armazenar seus arquivos; </li>
	<li> Lan�ar a freq��ncia dos alunos de forma integrada com o Di�rio de Classe; </li>
	<li> Colocar um cronograma de aulas informando o conte�do que ser� abordado; </li>
	<li> Visualizar os participantes da turma: todos os participantes da turma poder�o visualizar uns aos outros, sejam discentes ou docentes; </li>
	<li> Imprimir o di�rio de classe: docente poder� imprimir o di�rio de classe da turma; </li>
	<li> Cadastrar avalia��es: docente poder� agendar avalia��es da turma, as quais ser�o visualizadas pelos discentes matriculados na turma;</li>
	<li> Editar informa��es da turma: docente poder� editar o plano de trabalho do componente curricular da turma, o qual ser� visualizado pelos discentes da turma. Nesta op��o, o docente tamb�m poder� cadastrar as permiss�es dos alunos no portal da turma: cria��o de f�runs, cria��o de enquetes, visualiza��o de notas de todos os alunos da turma, etc.</li>
	<li> Cadastrar e consolidar notas da turma: o docente poder� cadastrar as notas das avalia��es que ele cadastrou, bem como consolid�-las atrav�s do portal da turma;</li>
	<li> Cadastrar not�cias da turma: o docente poder� cadastrar not�cias referentes � turma que est� ministrando, elas ser�o visualizadas pelos alunos dessa turma;
	<li> Cadastrar Mural: o mural � um f�rum criado para postagens de discuss�es espec�ficas do componente da turma. O discente poder� visualizar e responder tais quest�es;</li>
	<li> Adicionar permiss�o: permite que o docente da turma d� permiss�o especial a um outro docente, aluno de mestrado, etc, a fim de que este possa, por exemplo, anexar material referente � aula ministrada para a turma;</li>
	<li> Inserir refer�ncias bibliogr�ficas: docente poder� inserir refer�ncias bibliogr�ficas inerentes ao componente ministrado, que ser�o visualizadas pelos discentes matriculados;</li>
	<li> Cadastrar f�rum: O docente e os alunos da turma, desde que tenha sido dada permiss�o pelo docente, poder�o cadastrar f�runs para discuss�o.</li>
	<li> Cadastrar enquete: : O docente e os alunos da turma, desde que tenha sido dada permiss�o pelo docente, poder�o cadastrar e enquetes.</li>
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