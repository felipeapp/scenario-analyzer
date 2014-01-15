<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
<!--
	h2{
		padding-top: 1cm;
		padding-bottom: 1cm;
		font-size: 1.5em;
		text-align: center;
		letter-spacing: 0.4em;
		word-spacing: 0.4em;
	}
	.titulo{
		font-size: 1.3em;
		text-align: center;
	}
-->
</style>

<h2 style="border-bottom: 0;">
	DECLARAÇÃO
</h2>

<p style="text-align: justify; font-size: 1.3em; line-height: 1.7em">

	Declaramos, para os devidos fins, que o aluno <b>${declaracaoDefesaMBean.discente.pessoa.nome}</b> foi aprovado(a) na ${declaracaoDefesaMBean.bancaPos.tipoDescricao}
	<c:if test="${declaracaoDefesaMBean.bancaPos.mestrado}"> de DISSERTAÇÃO </c:if><c:if test="${declaracaoDefesaMBean.bancaPos.doutorado}"> de TESE </c:if> em ${declaracaoDefesaMBean.discente.curso.descricao} do Curso de ${declaracaoDefesaMBean.bancaPos.nivel}, no dia ${declaracaoDefesaMBean.bancaPos.descricaoDataHora},
	no(a) ${declaracaoDefesaMBean.bancaPos.local}, ${ configSistema['siglaInstituicao'] }, cuja banca examinadora fora constituída  
	 ${fn:length(declaracaoDefesaMBean.bancaPos.membrosBanca) > 1 ? ' pelos professores: ' : ' pelo professor: '}

	<p style="text-align: center; font-size: 1.3em; line-height: 1.7em">
		<c:forEach var="item" items="${declaracaoDefesaMBean.bancaPos.membrosBanca}">
			<c:if test="${!empty item.tipoDescricaoTitulo}">
				${item.tipoDescricaoTitulo} (a)
			</c:if> ${item.nome}
			<br />
			(${item.tipoDescricao})
			<br />
		</c:forEach>
	</p>
	<br/>
	<p style="text-align: justify; font-size: 1.3em; line-height: 1.7em">
		A sua <c:if test="${declaracaoDefesaMBean.bancaPos.mestrado}"> DISSERTAÇÃO </c:if><c:if test="${declaracaoDefesaMBean.bancaPos.doutorado}"> TESE </c:if> intitulou-se:
	</p>
	<br/>
	<div class="titulo">
		${declaracaoDefesaMBean.bancaPos.dadosDefesa.titulo}
	</div>
	<br/><br/>
	
	<p style="text-align: right;">
		<i>Esta declaração não exclui o aluno de efetuar as mudanças<br/> 
		sugeridas pela banca nem vale como outorga de grau de ${declaracaoDefesaMBean.bancaPos.nivel}, de acordo com o<br/>
		definido na Resolução 072/2004-CONSEPE.</i>
	</p>
	
	<p align="right" style="font-size: 1.3em; padding-top: 2cm; padding-bottom: 2.5cm">
		${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" name="dataAtual" />.
	</p>

	<div align="center">_______________________________________________________________________
		<br/>
			${declaracaoDefesaMBean.coordenadorCurso}
		<br/>
			COORDENADOR(A) ${declaracaoDefesaMBean.programaStricto.nome}
	</div>
</P>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>