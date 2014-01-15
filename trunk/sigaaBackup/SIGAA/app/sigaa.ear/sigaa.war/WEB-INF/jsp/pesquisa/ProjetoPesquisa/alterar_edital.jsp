<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<ufrn:keepAlive tempo="5"/>

<h2> <ufrn:steps/> </h2>

<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" method="post">

    <table class="formulario" align="center" width="95%" cellpadding="4" cellspacing="2">
    <caption class="listagem">Informe do Projeto de Pesquisa</caption>

	<c:if test="${not empty projetoPesquisaForm.obj.codigo.prefixo }">
	<tr>
		<th><b> C�digo do Projeto: </b></th>
		<td>
			${ projetoPesquisaForm.obj.codigo }
		</td>
	</tr>
	</c:if>
	<tr>
		<th><b> Tipo do Projeto: </b> </th>
		<td>
			${ projetoPesquisaForm.obj.interno ? "INTERNO" : "EXTERNO" }
		</td>
	<tr>
	<tr>
		<th width="25%"><b> T�tulo: </b></th>
		<td>
			${projetoPesquisaForm.obj.titulo}
		</td>
	</tr>
	<tr>
		<th> <b>Unidade:</b> </th>
		<td>
			${ projetoPesquisaForm.obj.unidade }
		</td>
	</tr>

	<c:choose>
		<c:when test="${ projetoPesquisaForm.obj.interno && projetoPesquisaForm.obj.edital != null}">
			<c:if test="${!projetoPesquisaForm.segundaChamada}">
			<tr>
				<th class="required">Edital de Pesquisa:</th>
				<td>
					<c:set var="editaisAbertos" value="${projetoPesquisaForm.referenceData.editaisAbertos }" />
					<html:select property="obj.edital.id" style="width:90%" 
						onchange="document.getElementById('dispatch').value = 'carregarDatas'; submit();">
						<html:option value="-1"> -- SELECIONE UMA OP��O --  </html:option>
				        <html:options collection="editaisAbertos" property="id" labelProperty="descricao" />
			        </html:select>
				</td>
			</tr>
			
			<c:if test="${ projetoPesquisaForm.exibirAnos }">
				<tr>
					<td colspan="2">
						<div class="descricaoOperacao">
							<p>
								Este edital permite a submiss�o de projetos com dura��o m�xima at� 
									<ufrn:format type="data" valor="${projetoPesquisaForm.obj.edital.fimExecucaoProjetos}"></ufrn:format>
									. O per�odo de execu��o escolhido refletir� no cronograma a ser preenchido posteriormente. 
									Caso escolha um per�odo superior a 1(um) ano, o projeto ser� renovado automaticamente pelo sistema 
									a cada ano.
							</p>
						</div>	
					</td>	
				</tr>	
				<tr>			
					<th>Per�odo Execu��o:</th>
					<td>
						de: 
							<ufrn:format type="data" valor="${projetoPesquisaForm.obj.edital.inicioExecucaoProjetos}"></ufrn:format>
						at�:
						<c:set var="anos" value="${projetoPesquisaForm.referenceData.anos}" />
						<html:select property="obj.tempoEmAnoProjeto" style="width:15%">
					        <html:options collection="anos" property="qntAnos" labelProperty="dataFormatada" />
				        </html:select>
					</td>
				</tr>
			</c:if>
			</c:if>
		</c:when>
	</c:choose>

	<tfoot>
		<tr>
		<td colspan="2">
			<html:button dispatch="gravar" value="Cadastrar"/>
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true" />
		</td>
		</tr>
	</tfoot>
	</table>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>