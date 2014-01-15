<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form id="formAviso">
		
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Solicitação de Bolsa Auxílio</h2>
	
	<div class="descricaoOperacao">
		
		<center><b>POR FAVOR, LEIA AS INSTRUÇÕES ABAIXO </b></center>
		
		<br/>		
			<center>
				<b>PERÍODOS DE INSCRIÇÃO</b>
			</center>
		
		<c:set var="conteudos" value="#{bolsaAuxilioMBean.anoReferencia.calendario}"/>
		<c:if test="${not empty conteudos}">
			<table width="350" align="center">
				<thead>
					<tr>
						<td>Tipo da Bolsa</td>
						<td>&nbsp;</td>
						<td style="text-align: center;">Início</td>
						<td>&nbsp;</td>
						<td style="text-align: center;">Fim</td>
						<td>&nbsp;</td>
						<td style="text-align: center;" nowrap="true">Hora término</td>
						<td>&nbsp;</td>
						<td>Município</td>
						<td>&nbsp;</td>
						<td>Discentes permitidos</td>
					</tr>
				</thead>
				<c:forEach var="item" items="#{bolsaAuxilioMBean.anoReferencia.calendario}">
					<tr>
						<td nowrap="true">${ item.tipoBolsaAuxilio.denominacao }</td>
						<td>&nbsp;</td>
						<td style="text-align: center;"><fmt:formatDate pattern="dd/MM/yyyy" value="${item.inicio}"/> </td>
						<td>&nbsp;</td>
						<td style="text-align: center;"><fmt:formatDate pattern="dd/MM/yyyy" value="${item.fim}"/> </td>
						<td>&nbsp;</td>
						<td style="text-align: center;">
							<c:choose>
								<c:when test="${ not empty item.horaTermino && not empty item.minutoTermino }">
									${item.horaTermino}:${item.minutoTermino}
								</c:when>
							</c:choose>  
						 </td>
						<td>&nbsp;</td>
						<td nowrap="true">${item.municipio.nome}</td>
						<td>&nbsp;</td>
						<td nowrap="true">
							<c:if test="${item.alunoNovato == true}">Discentes novatos</c:if> <c:if test="${item.alunoVeterano == true}">/</c:if>
							<c:if test="${item.alunoVeterano == true}">Discentes veteranos</c:if>
						</td>
					</tr>
				</c:forEach>		
			</table>
		</c:if>
		
		<br/>
		<hr>	
		<br/>
		
		<h:outputText value="#{bolsaAuxilioMBean.anoReferencia.textoTelaAvisoDiscentes}" escape="false"></h:outputText>
		
		
		<!-- VISUALIZAÇÃO DO ANEXO -->
		
		<c:if test="${ bolsaAuxilioMBean.anoReferencia.possuiArquivo }">
			<center>
				<a href="${ctx}/verProducao?idProducao=${ bolsaAuxilioMBean.anoReferencia.idArquivo }&&key=${ sf:generateArquivoKey(bolsaAuxilioMBean.anoReferencia.idArquivo) }"
					target="_blank" title="Fazer download do arquivo">
						Clique aqui para fazer o download do anexo
				</a>
			</center>
		</c:if>
		
		<br/>
		<hr>	
		<br/>
		

	<table style="width: 100%" class="subFormulario">
			
			<tr>
				<th class="obrigatorio">Bolsa Desejada:</th>
				<td>
					<h:selectOneMenu immediate="true" value="#{bolsaAuxilioMBean.obj.tipoBolsaAuxilio.id}">
						<f:selectItem  itemLabel="-- SELECIONE -- " itemValue="0" /> 
						<f:selectItems value="#{calendarioBolsaAuxilioMBean.tiposBolsaAuxilio}" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{bolsaAuxilioMBean.obj.termoConcordancia}" id="idTermoConcordancia" /></th>
				<td>Eu li e compreendi as instruções e prazos informados acima.</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Continuar >>>" action="#{bolsaAuxilioMBean.solicitacaoBolsaAuxilio}" />
					</td>
				</tr>
			</tfoot>
	</table>
	
	</h:form>
	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>