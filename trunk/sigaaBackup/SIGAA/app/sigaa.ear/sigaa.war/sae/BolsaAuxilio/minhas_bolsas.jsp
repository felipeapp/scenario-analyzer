<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
</style>
<f:view>
	<h:messages/>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Buscar Bolsa Aux�lio por Aluno</h2>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Bolsa Aux�lio
<%-- 	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Formul�rio --%>
	    <h:graphicImage value="/img/extensao/printer.png" style="overflow: visible;"/>: Imprimir Comprovante
	</div>

	<h:form>
	
	<table class="listagem" width="100">
	
	<caption> Bolsas Cadastradas </caption>
	<thead>
		<tr>
			<th class="alinharDireita">Ano/Per�odo</th>
			<th class="alinharCentro">Matr�cula</th>
			<th>Discente</th>
			<th>Tipo da bolsa SAE</th>
			<th>Tipo da Bolsa SIPAC</th>
			<th class="alinharEsquerda">Situa��o</th>
			<th class="alinharCentro">Data cadastro</th>
			<th></th>
			<th></th>
			<th></th>
	</thead>

		<tbody>
			<c:forEach items="#{buscarBolsaAuxilioMBean.bolsas}" 
				var="bolsaAuxPeriodo" varStatus="loop">
			
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					
					<td class="alinharCentro">${bolsaAuxPeriodo.ano}.${bolsaAuxPeriodo.periodo}</td>
					<td class="alinharCentro">${bolsaAuxPeriodo.bolsaAuxilio.discente.matricula}</td>
					<td>${bolsaAuxPeriodo.bolsaAuxilio.discente.pessoa.nome}</td>
					<td>${bolsaAuxPeriodo.bolsaAuxilio.tipoBolsaAuxilio.denominacao}</td>
					<td>${bolsaAuxPeriodo.bolsaAuxilio.descricaoTipoBolsaSIPAC}</td>
					<td class="alinharEsquerda">${bolsaAuxPeriodo.bolsaAuxilio.situacaoBolsa.denominacao}</td>
				    <td class="alinharCentro"><fmt:formatDate pattern="dd/MM/yyyy" value="${bolsaAuxPeriodo.bolsaAuxilio.dataSolicitacao}"/></td>
		
					<td>
						<h:commandLink action="#{ buscarBolsaAuxilioMBean.atualizar }">
							<f:param name="id" value="#{ bolsaAuxPeriodo.bolsaAuxilio.id }"/>
							<f:param name="imprimir" value="false"/>
							<h:graphicImage value="/img/alterar.gif" title="Alterar Bolsa Aux�lio" />
						</h:commandLink> 		
					</td>
					<td>
						<h:commandLink action="#{ bolsaAuxilioMBean.imprimirComprovanteSAE }">
								<f:param name="id" value="#{ bolsaAuxPeriodo.bolsaAuxilio.id }"/>
								<f:param name="imprimir" value="true"/>
							 	<h:graphicImage value="/img/extensao/printer.png" title="Imprimir Comprovante" />
						</h:commandLink> 		
					</td>
					
				</tr>
			</c:forEach>	
		</tbody>
	</table>
	<br/><br/>
		<center><input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" /></center>	
	</h:form>
	<br/>		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>