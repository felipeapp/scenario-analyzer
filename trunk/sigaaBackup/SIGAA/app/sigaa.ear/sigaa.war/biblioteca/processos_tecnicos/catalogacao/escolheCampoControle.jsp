<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Campo de Controle</h2>
<f:view>

<%-- 

	<h:form id="form">
	
	<div class="infoAltRem" style="width: 60%">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:Selecionar Campo de Controle
	</div>
	
	
	<table class=formulario width="60%">
		<caption>Campos de Controle</caption>
		
		<thead>
			<tr>
				<td>Descrição</td>
				<td width="1%"></td>
			</tr>
		</thead>
		
		<%-- Estah fixo aqui porque como as etiquetas de controle fazem parte de um padrao 
		<%-- Niguem pode mudar isso, entao nao precisa ficar buscando no banco porque dificilmente isso vai mudar 
		
		<tr class="linhaPar">
			<td>LDR - LÍDER (NR)</td>
			<td>
				<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" action="#{catalogacaoMBean.selecionarFormatoTitulo}">
					<f:param name="tagEscolhidaAdicaoCampoControle" value="LDR"/>
					<h:graphicImage value="/img/seta.gif"/>
				</h:commandLink>
			</td>
		</tr>
		<tr class="linhaImpar">
			<td>001 - NÚMERO DE CONTROLE (NR) </td>
			<td>
				<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" action="#{catalogacaoMBean.selecionarFormatoTitulo}">
					<f:param name="tagEscolhidaAdicaoCampoControle" value="001"/>
					<h:graphicImage value="/img/seta.gif"/>
				</h:commandLink>
			</td>
		</tr>
		<tr class="linhaPar">
			<td>003 - IDENTIFICADOR DO NÚMERO DE CONTROLE (NR) </td>
			<td>
				<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" action="#{catalogacaoMBean.selecionarFormatoTitulo}">
					<f:param name="tagEscolhidaAdicaoCampoControle" value="003"/>
					<h:graphicImage value="/img/seta.gif"/>
				</h:commandLink>
			</td>
		</tr>
		<tr class="linhaImpar">
			<td>005 - DATA E HORA DA ÚLTIMA INTERVENÇÃO (NR) </td>
			<td>
				<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" action="#{catalogacaoMBean.selecionarFormatoTitulo}">
					<f:param name="tagEscolhidaAdicaoCampoControle" value="005"/>
					<h:graphicImage value="/img/seta.gif"/>
				</h:commandLink>
			</td>
		</tr>
		<tr class="linhaPar">
			<td>006 - CAMPOS DE TAMANHO FIXO</td>
			<td>
				<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" action="#{catalogacaoMBean.selecionarFormatoTitulo}">
					<f:param name="tagEscolhidaAdicaoCampoControle" value="006"/>
					<h:graphicImage value="/img/seta.gif"/>
				</h:commandLink>
			</td>
		</tr>
		<tr class="linhaImpar">
			<td>007 - CAMPOS FIXOS DE DESCRIÇÃO FÍSICA</td>
			<td>
				<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" action="#{catalogacaoMBean.selecionarFormatoTitulo}">
					<f:param name="tagEscolhidaAdicaoCampoControle" value="007"/>
					<h:graphicImage value="/img/seta.gif"/>
				</h:commandLink>
			</td>
		</tr>
		<tr class="linhaPar">
			<td>008 - CAMPOS FIXOS DE DADOS</td>
			<td>
				<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" action="#{catalogacaoMBean.selecionarFormatoTitulo}">
					<f:param name="tagEscolhidaAdicaoCampoControle" value="008"/>
					<h:graphicImage value="/img/seta.gif"/>
				</h:commandLink>
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{catalogacaoMBean.cancelar}"/>
				</td>
			</tr>
		</tfoot>
		
	</table>
	</h:form>  --%>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>