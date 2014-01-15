<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<script type="text/javascript">
<!--
	function selectTecTurmaEntrada(curso, habilitacao, modalidade, estruturaCodigo, estruturaId, data, ano, periodo, id) {
		window.opener.document.getElementById('inputCurso').value=curso;
		window.opener.document.getElementById('inputHabilitacao').value=habilitacao;
		window.opener.document.getElementById('inputModalidade').value=modalidade;
		window.opener.document.getElementById('inputEstruturaCurricular').value=estruturaCodigo;
		window.opener.document.getElementById('inputTecEstruturaCurricularId').value=estruturaId;
		window.opener.document.getElementById('inputDataEntrada').value=data;
		window.opener.document.getElementById('inputAnoReferencia').value=ano;
		window.opener.document.getElementById('inputPeriodoReferencia').value=periodo;
		window.opener.document.getElementById('inputTecTurmaEntradaId').value=id;
		javascript:window.close();
	}
//-->
</script>
<h2 class="tituloPagina">
	<fmt:message key="titulo.buscar">
		<fmt:param value="Turma de Entrada de Técnico"/>
	</fmt:message>
</h2>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/ensino/tecnico/buscarNomeTecTurmaEntrada" method="post" focus="tipoTurmaEntrada">
	<div class="areaDeDados">
		<h2>Busca por Turma de Entrada de Técnico</h2>
		<div class="dados">
            <div class="areaDeDados2">
				<h2><input type="radio" name="tipoBusca" value="1" class="noborder"/>Estrutura Curricular:</h2>
    	    	<div class="dados">
            		<div class="head">Código:</div>
		    	    <div class="texto">
        		       	<input 	type="text" name="nomeestruturacurricular" readonly="readonly" maxlength="100" size="40" id="inputEstruturaCurricular"
               					value="${tecTurmaEntrada.tecEstruturaCurricular.id}" onmouseover="this.style.cursor='pointer'"
               					onClick="window.open('${pageContext.request.contextPath}/ensino/tecnico/verTelaBuscaTecEstruturaCurricular.do', '','width=670,height=350, top=270, left=250, scrollbars' )"/>
		               	<html:hidden property="tecEstruturaCurricular" value="${tecTurmaEntrada.tecEstruturaCurricular.id}" styleId="inputTecEstruturaCurricularId"/>
						<span 	class="search" onmouseover="this.style.cursor='pointer'"
								onClick="window.open('${pageContext.request.contextPath}/ensino/tecnico/verTelaBuscaTecEstruturaCurricular.do', '','width=670,height=350, top=270, left=250, scrollbars' )">&nbsp;</span>
					</div>

        		    <div class="head">Curso:</div>
		   	        <div class="texto">
	               	<input 	type="text" name="nomeestruturacurricular" readonly="readonly" maxlength="100" size="40" id="inputCurso" value="${tecEstruturaCurricular.tecModalidade.tecHabilitacao.tecCurso.nome}"
	               			onmouseover="this.style.cursor='pointer'"
	               			onClick="window.open('${pageContext.request.contextPath}/ensino/tecnico/verTelaBuscaTecEstruturaCurricular.do', '','width=670,height=350, top=270, left=250, scrollbars' )"/>
					</div>

					<div class="head">Modalidade:</div>
		   	        <div class="texto">
        		       	<input 	type="text" name="nomeestruturacurricular" readonly="readonly" maxlength="100" size="40" id="inputModalidade" value="${tecEstruturaCurricular.tecModalidade.tipoModalidade.descricao}"
        		       			onmouseover="this.style.cursor='pointer'"
								onClick="window.open('${pageContext.request.contextPath}/ensino/tecnico/verTelaBuscaTecEstruturaCurricular.do', '','width=670,height=350, top=270, left=250, scrollbars' )"/>
					</div>

        		  	<div class="head">Habilitação:</div>
   	        		<div class="texto">
               			<input 	type="text" name="nomeestruturacurricular" readonly="readonly" maxlength="100" size="40" id="inputHabilitacao" value="${tecEstruturaCurricular.tecModalidade.tecHabilitacao.nome}"
               					onmouseover="this.style.cursor='pointer'"
               					onClick="window.open('${pageContext.request.contextPath}/ensino/tecnico/verTelaBuscaTecEstruturaCurricular.do', '','width=670,height=350, top=270, left=250, scrollbars' )"/>
					</div>
          		</div>
          	</div>

            <div class="head">Ano de Referência</div>
            <div class="texto">
            	<html:text property="anoReferencia" maxlength="4" size="8" onfocus="javascript:forms[0].tipoBusca[0].checked = true;" />
            </div>

            <div class="head">Período de Referência</div>
            <div class="texto">
            	<html:text property="periodoReferencia" maxlength="2" size="4" onfocus="javascript:forms[0].tipoBusca[0].checked = true;" />
            </div>

            <div class="head"><input type="radio" name="tipoBusca" value="3" class="noborder"/> Todos</div>

            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div>
	</div>
</html:form>
<c:if test="${not empty tecTurmaEntradas}">
	<br>
	<br>
	<div class="areaDeDados lista">
	    <h2>Turmas de Entrada de Técnico Encontradas</h2>
	    <table>
	        <thead>
	        <th>Estrutura Curricular</th>
	        <th>Data de Entrada</th>
	        <th>Ano de Referência</th>
	        <th>Período de Referência</th>
	        <th>&nbsp;</th>
	        <tbody>

			<c:forEach items="${tecTurmaEntradas}" var="tecTurmaEntrada">
				<tr>
					<td> ${tecTurmaEntrada.tecEstruturaCurricular.tecModalidade.tecHabilitacao.tecCurso.nome} - ${tecTurmaEntrada.tecEstruturaCurricular.tecModalidade.tecHabilitacao.nome} - ${tecTurmaEntrada.tecEstruturaCurricular.tecModalidade.tipoModalidade.descricao} - ${tecTurmaEntrada.tecEstruturaCurricular.codigo} </td>
                    <td> ${tecTurmaEntrada.dataEntrada} </td>
                    <td> ${tecTurmaEntrada.anoReferencia} </td>
                    <td> ${tecTurmaEntrada.periodoReferencia} </td>
					<td width="15">
						<html:img 	page="/img/seta.gif" alt="Selecionar Turma de Entrada de Técnico" title="Selecionar Turma de Entrada de Técnico"
									border="0" onmouseover="this.style.cursor='pointer'"
									onclick="selectTecTurmaEntrada('${tecTurmaEntrada.tecEstruturaCurricular.tecModalidade.tecHabilitacao.tecCurso.nome}','${tecTurmaEntrada.tecEstruturaCurricular.tecModalidade.tecHabilitacao.nome}','${tecTurmaEntrada.tecEstruturaCurricular.tecModalidade.tipoModalidade.descricao}','${tecTurmaEntrada.tecEstruturaCurricular.codigo}','${tecTurmaEntrada.tecEstruturaCurricular.id}','${tecTurmaEntrada.dataEntrada}','${tecTurmaEntrada.anoReferencia}','${tecTurmaEntrada.periodoReferencia}','${tecTurmaEntrada.id}');" />
					</td>
				</tr>
			</c:forEach>
			</tbody>
	    </table>
	</div>
</c:if>
