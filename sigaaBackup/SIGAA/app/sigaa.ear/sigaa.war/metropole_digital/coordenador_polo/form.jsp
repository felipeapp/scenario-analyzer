<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<a4j:keepAlive beanName="coordenadorPoloIMD"/>
<f:view>
	<c:if test="${coordenadorPoloIMD.coordenador.id <= 0}">
		<h2><ufrn:subSistema /> > Cadastro de Coordenador de Pólo do IMD</h2>
	</c:if>
	<c:if test="${coordenadorPoloIMD.coordenador.id > 0}">
		<h2><ufrn:subSistema /> > Alterar Coordenador de Pólo do IMD</h2>
	</c:if>
	
	<h:form>
		<div class="infoAltRem" style="width: 60%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			<a href="${ctx}/metropole_digital/coordenador_polo/lista.jsf" >Listar Coordenadores de Pólo do IMD</a>
		</div>
    
		<table class="formulario" width="60%">
			<c:if test="${coordenadorPoloIMD.coordenador.id <= 0}">
				<caption class="listagem">Cadastro de Coordenador de Pólo do IMD</caption>
			</c:if>
			<c:if test="${coordenadorPoloIMD.coordenador.id > 0}">
				<caption class="listagem">Alterar Coordenador de Pólo do IMD</caption>
			</c:if>
			<tr>
				<td colspan="2">
					<h:inputHidden value="#{coordenadorPoloIMD.coordenador.pessoa.id}"/>
					<h:inputHidden value="#{coordenadorPoloIMD.coordenador.id}"/>
				</td>
			<tr>
			<tr>
				<th style="width: 30%;" align="right"><b>Nome: </b></th>
				<td style="width: 70%;" align="left">${coordenadorPoloIMD.coordenador.pessoa.nome}</td>
			</tr>
			<tr>
				<th style="width: 30%;" align="right"><b>CPF: </b></th>
				<td style="width: 70%;" align="left"><ufrn:format type="cpf_cnpj" valor="${coordenadorPoloIMD.coordenador.pessoa.cpf_cnpj}" /></td>
			</tr>
			<tr><td colspan="2"><br /></td><tr>
			
			<tr>
				<th class="obrigatorio">Pólo:</th>
				<td>
					<h:selectOneMenu value="#{ coordenadorPoloIMD.coordenador.polo.id }" id="selectPolos">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ coordenadorPoloIMD.polosCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			
			
		<!-- Botões -->
		
		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${coordenadorPoloIMD.coordenador.id <= 0}">
						<h:commandButton value="#{coordenadorPoloIMD.confirmButton}" action="#{coordenadorPoloIMD.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{coordenadorPoloIMD.cancelar}" onclick="#{confirm}"/>
					</c:if> 
					<c:if test="${coordenadorPoloIMD.coordenador.id > 0 }">
						<h:commandButton value="Alterar" action="#{coordenadorPoloIMD.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{coordenadorPoloIMD.cancelarAlteracao}" onclick="#{confirm}"/>
					</c:if> 
				</td>
			</tr>
		</tfoot>
	 	<!-- Fim botões -->
	 	
	 	</table>
		
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>