<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/stricto/menu_coordenador.jsp" %>
<h2><ufrn:subSistema /> > Alterar Limites de Orientandos de Docentes do Programa</h2>

<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>	Esta operação destina-se alterar o número máximo de orientandos dos docentes de pós-graduação.
	Abaixo encontra-se a lista dos docentes. Entre com o número máximo de orientandos de cada docente. </p>
	<p>	Se o campo tiver o valor 0 (zero) indica que o docente não pode ter orientando. </p>
	<p>	Se o campo não tiver nenhum valor indica que não há limite de orientando para este docente. </p>
</div>

<h:form>
<h:messages showDetail="true"/>


<center>
	<div class="infoAltRem">
		MORM: Máximo de Orientandos Regulares Mestrado&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		MORD: Máximo de Orientandos Regulares Doutorado
		<br/>
		MOEM: Máximo de Orientandos Especiais Mestrado&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		MOED: Máximo de Orientandos Especiais Doutorado		
	</div>
</center>

<t:dataTable var="equipe" value="#{equipePrograma.equipeDoPrograma}" styleClass="listagem" rowClasses="linhaPar,linhaImpar">
	<f:facet name="caption">
	<f:verbatim>Docentes do Programa ${fn:length(equipePrograma.equipeDoPrograma)} docentes</f:verbatim></f:facet>

	<h:column>
		<f:facet name="header">
			<f:verbatim>Docente</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.nome}"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>Vínculo</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.vinculo}"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>Nível</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.nivel}"/>
	</h:column>

	<h:column>
		<f:facet name="header">
			<f:verbatim>Mest.</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.mestrado ? 'Sim' : 'Não'}"/>
	</h:column>

	<h:column>
		<f:facet name="header">
			<f:verbatim>Dout.</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.doutorado ? 'Sim' : 'Não'}"/>
	</h:column>

	
	<h:column>
		<f:facet name="header">
			<f:verbatim>MORM</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoRegularMestrado}" size="2" maxlength="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoRegularMestrado"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>MORD</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoRegularDoutorado}" size="2" maxlength="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoRegularDoutorado"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>MOEM</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoEspecialMestrado}" maxlength="2" size="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoEspecialMestrado"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>MOED</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoEspecialDoutorado}" maxlength="2" size="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoEspecialDoutorado"/>
	</h:column>	
</t:dataTable>

<br/>
<p align="center">
	<h:commandButton value="Gravar Limites" action="#{equipePrograma.cadastrarLimitesOrientandos}" id="botaoGravarLimites"/>
</p>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>