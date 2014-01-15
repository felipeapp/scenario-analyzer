<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Gerar Mapa Acesso RU </h2>
	<br>

	<a4j:keepAlive beanName="diasAlimentacaoMBean" />

	<h:form id="form">
	
		<table class="formulario" width="60%">
			<caption class="listagem">Selecionar o ano e período para realizar a busca</caption>

			<tr>
				<th> Ano-Periodo: </th>
				<td>
				    <h:inputText value="#{ diasAlimentacaoMBean.anoRefSae.ano }" size="3" maxlength="4"/> - 
					<h:inputText value="#{ diasAlimentacaoMBean.anoRefSae.periodo }" size="1" maxlength="1"/> 
				</td> 
			</tr>

			<tr>
				<th> Tipo da bolsa: </td>
				<td> 
					<h:selectOneMenu value="#{diasAlimentacaoMBean.tipoBolsa.id}">
						<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
						<f:selectItems value="#{ relatoriosSaeMBean.allTiposBolsasCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>			
				<th> Deferimento: </th>
				<td> 
					<h:selectOneRadio value="#{diasAlimentacaoMBean.situacao.id}" layout="pageDirection">
						<f:selectItems value="#{situacaoBolsaAuxilioMBean.allCombo}" />
					</h:selectOneRadio>
				</td>
			</tr>
			
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Buscar" action="#{ diasAlimentacaoMBean.relatorioMapaAcessoRU }"/>
					<h:commandButton value="Cancelar" action="#{diasAlimentacaoMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>

		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>