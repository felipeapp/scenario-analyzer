<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina">
<ufrn:subSistema></ufrn:subSistema> &gt; Lista de Discentes Pré-Inscritos
</h2>

<ufrn:table collection="${lista}"
	properties="pessoa.nome, curso.descricao"
	headers="Nome, Curso"
	title="Alunos Pré-Inscritos" crud="false"
	links="src='${ctx}/img/seta.gif',/sigaa/pessoa/wizard.do?dispatch=popular&nextView=dadosDiscente&pessoaId={pessoa.id}&cursoId={curso.id}"
	linksRoles="<%= new int[][] {new int[] {SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO}}%>" />

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
