<%@ taglib uri="/tags/format" prefix="fmt"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>
<ufrn:steps/>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<style>
.areaDeDados {
  width: 85%;
}
.areaDeDados .dados .texto {
  margin-left: 20em;
}
.areaDeDados2 {
	width: 85%;
}
.areaDeDados2 h2 {
  font-size: 8pt;
  margin-top: 0;
  padding-left: 5px;
  padding-bottom: 5px;
}

</style>
<html:form action="/pesquisa/analisarProjetoExterno" method="post">

	<!-- dados do projeto -->
    <table class="formulario" align="center" width="100%">
    <caption class="listagem">Dados do Projeto de Pesquisa</caption>
    <tbody>
	<tr>
		<th>
		Código: 
		</th>
		<td width="80%">
		 ${projetoPesquisaForm.obj.codigo}
		</td>
	</tr>

	<tr>
		<th>
		Titulo: 
		</th>
		<td>
		 ${projetoPesquisaForm.obj.nome}
		</td>
	</tr>
	<tr>
		<th>
		Descrição:
		</th>
		<td>
		<ufrn:format type="texto" name="projetoPesquisaForm" property="obj.descricao"></ufrn:format>
		</td>
	</tr>
	<tr>
		<th>
		Email:
		</th>
		<td>
		 ${projetoPesquisaForm.obj.email}
		</td>
	</tr>
	<tr>
		<th>
		Area:
		</th>
		<td>
			${projetoPesquisaForm.obj.areaConhecimentoCnpq.nome}
		</td>
	</tr>

	<tr>
		<th>
		Data Início:
		</th>
		<td>
		<fmt:formatDate value="${projetoPesquisaForm.obj.dataInicio}"pattern="dd/MM/yyyy" />
		</td>
	</tr>

	<tr>
		<th>
		Data Fim:
		</th>
		<td>
		<fmt:formatDate value="${projetoPesquisaForm.obj.dataFim}"pattern="dd/MM/yyyy" />
		</td>
	</tr>

	<tr><td colspan="2"><hr/></td></tr>

	<!-- DADOS DO PROJETO -->
	<tr>
		<th>
		Introdução
		</th>
		<td>
		<ufrn:format type="texto" name="projetoPesquisaForm" property="obj.introducao"></ufrn:format>
		</td>
	</tr>

	<tr><td colspan="2"><hr/></td></tr>
	
	<tr>
		<th>
		Objetivos
		</th>
		<td>
		<ufrn:format type="texto" name="projetoPesquisaForm" property="obj.objetivos"></ufrn:format>
		</td>
	</tr>	

	<tr><td colspan="2"><hr/></td></tr>
	
	<tr>
		<th>
		Justificativa
		</th>
		<td>
		<ufrn:format type="texto" name="projetoPesquisaForm" property="obj.justificativa"></ufrn:format>
		</td>
	</tr>	

	<tr><td colspan="2"><hr/></td></tr>
	
	<tr>
		<th>
		Metodologia
		</th>
		<td>
		<ufrn:format type="texto" name="projetoPesquisaForm" property="obj.metodologia"></ufrn:format>
		</td>
	</tr>	
	
	<tr><td colspan="2"><hr/></td></tr>
	
	<tr>
		<th>
		Bibliografia
		</th>
		<td>
		<ufrn:format type="texto" name="projetoPesquisaForm" property="obj.bibliografia"></ufrn:format>
		</td>
	</tr>	
	
	<tr><td colspan="2"><hr/></td></tr>
	
	<!-- visualização do cronograma -->
	<c:if test="${not empty projetoPesquisaForm.telaCronograma.mesesAno}">	
		<tr> <td colspan="2" style="margin:0; padding: 0;">
			<table id="cronograma" class="subFormulario" width="100%">
			<caption> Cronograma de Atividades </caption>
			<thead>
				<tr>
					<th width="30%" rowspan="2"> Atividade </th>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano">
					<th colspan="${fn:length(ano.value)}" align="center" class="inicioAno fimAno">
						${ano.key}
					</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano">
						<c:forEach items="${ano.value}" var="mes" varStatus="status">
						<c:set var="classeCabecalho" value=""/>
						<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
						<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>
						
						<th class="${classeCabecalho}"> ${mes}	</th>
						</c:forEach>
					</c:forEach>			
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades" value="${fn:length(projetoPesquisaForm.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes" value="${fn:join(projetoPesquisaForm.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
				<tr>	
					<th> ${projetoPesquisaForm.telaCronograma.atividade[statusAtividades.index-1]} </th>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
						<c:forEach items="${ano.value}" var="mes">
							<c:set var="valorCheckbox" value="${statusAtividades.index-1}_${mes}_${ano.key}" />
							<c:set var="classeCelula" value=""/>
							<c:if test="${fn:contains(valoresCheckboxes, valorCheckbox)}"> 
								<c:set var="classeCelula" value="selecionado"/>
							</c:if>
							<td align="center" class="${classeCelula}" > 
								&nbsp;
							</td>
						</c:forEach>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>	
			</table>
			</td>
		</tr>
	</c:if>
		<!-- FIM da visualização do cronograma -->	



	<!-- fim de dados do projeto -->
	
	
	<!-- lista de membros do projeto -->
	<c:if test="${not empty projetoPesquisaForm.obj.membrosProjetoServidor}">
		<tr> <td colspan="2" style="margin:0; padding: 0;">
	    <table class="subFormulario" width="100%">
		<caption class="listagem">Docentes</caption>
	        <thead>
		        <td>Matricula</td>
		        <td>Nome</td>
		        <td>Carga Horária Dedicada</td>
	        </thead>
	        <tbody>

	        <c:forEach items="${projetoPesquisaForm.obj.membrosProjetoServidor}" var="servidorProjeto">
	            <tr>
	                    <html:hidden property="servidorProjeto.servidor.id" value="${servidorProjeto.servidor.id}" />
	                    <td>${servidorProjeto.servidor.siape}</td>
	                    <td>${servidorProjeto.servidor.pessoa.nome}</td>
	                    <td  align="right">${servidorProjeto.chDedicadaProjeto}</td>
	            </tr>
	        </c:forEach>
	    </table>
	    </td></tr>
    </c:if>
	<!-- fim da lista de membros do projeto -->
	
	<!-- Lista de financiamentos do projeto -->	
	<c:if test="${not empty projetoPesquisaForm.obj.financiamentosProjetoPesq}">
		<tr> <td colspan="2" style="margin:0; padding: 0;">
		    <table class="subFormulario" width="100%">
			<caption class="listagem">Financiamentos</caption>
		        <thead>
			        <td>Entidade Financiadora</td>
			        <td>Programa da Entidade Financiadora</td>
			        <td>Natureza do Financiamento</td>
			        <td>Data Inicio</td>
			        <td>Data Fim</td>
		        </thead>
		        <tbody>
	
		        <c:forEach items="${projetoPesquisaForm.obj.financiamentosProjetoPesq}" var="financiamento">
		            <tr>
		                    <html:hidden property="financiamento.id" value="${financiamento.id}" />
		                    <td>${financiamento.programaEntidadeFinanciadora.entidadeFinanciadora.nome}</td>
		                    <td>${financiamento.programaEntidadeFinanciadora.nome}</td>
		                    <td>${financiamento.tipoNaturezaFinanc.descricao}</td>
		                    <td><fmt:formatDate value="${financiamento.dataInicio}"pattern="dd/MM/yyyy" /></td>
		                    <td><fmt:formatDate value="${financiamento.dataFim}"pattern="dd/MM/yyyy" /></td>
		            </tr>
		        </c:forEach>
		    </table>
	    </td></tr>
		    
	</c:if>

	</tbody>
	
	<tfoot>
		<tr>
			<td colspan="2">
		    		<html:button dispatch="aprovar" value="Aprovar"/>&nbsp;&nbsp;
		    		<html:button dispatch="reprovar" value="Reprovar"/>&nbsp;&nbsp;
		    		<html:button dispatch="cancelar" value="Cancelar"/><br>
			</td>
		</tr>
	</tfoot>
	</table>	    

</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
