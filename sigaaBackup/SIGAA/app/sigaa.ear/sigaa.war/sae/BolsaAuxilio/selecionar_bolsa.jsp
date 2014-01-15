<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages/>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Selecionar Bolsa Auxílio </h2>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Bolsa Auxílio
	</div>

	<h:form>
	  <table class="formulario" width="50%">
		
		<caption> Bolsas Encontradas </caption>
		<thead>
			<tr>
				<th>Tipo Bolsa</th>
				<th></th>
		</thead>
	
			<tbody>
				<c:forEach items="#{buscarBolsaAuxilioMBean.tiposBolsa}" var="bolsaAux" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${bolsaAux.denominacao}</td>
						<td width="5%">
							<h:commandLink action="#{ buscarBolsaAuxilioMBean.selecionaTipoBolsa }">
								<f:param name="id" value="#{ bolsaAux.id }"/>
								<h:graphicImage value="/img/seta.gif" title="Selecionar Bolsa Auxílio" />
							</h:commandLink> 		
						</td>
					</tr>
				</c:forEach>	
			</tbody>
	  </table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>