<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<a4j:keepAlive beanName="coordenadorTutorIMD"/>
<f:view>
	
	<h2><ufrn:subSistema /> > Cadastro de Coordenador de Tutores do IMD</h2>
	
	<h:form>
		<div class="infoAltRem" style="width: 60%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			<a href="${ctx}/metropole_digital/coordenador_polo/lista.jsf" >Listar Coordenadores de Tutores do IMD</a>
		</div>
    
		<table class="formulario" width="60%">
			<caption class="listagem">Cadastro de Coordenador de Tutores do IMD</caption>
			<tr>
				<td colspan="2">
					<h:inputHidden value="#{coordenadorTutorIMD.coordenador.pessoa.id}"/>
					<h:inputHidden value="#{coordenadorTutorIMD.coordenador.id}"/>
				</td>
			<tr>
			<tr>
				<th style="width: 30%;" align="right"><b>Nome: </b></th>
				<td style="width: 70%;" align="left">${coordenadorTutorIMD.coordenador.pessoa.nome}</td>
			</tr>
			<tr>
				<th style="width: 30%;" align="right"><b>CPF: </b></th>
				<td style="width: 70%;" align="left"><ufrn:format type="cpf_cnpj" valor="${coordenadorTutorIMD.coordenador.pessoa.cpf_cnpj}" /></td>
			</tr>
			
			
		<!-- Botões -->
		
		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${coordenadorTutorIMD.coordenador.id <= 0}">
						<h:commandButton value="#{coordenadorTutorIMD.confirmButton}" action="#{coordenadorTutorIMD.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{coordenadorTutorIMD.cancelar}" onclick="#{confirm}"/>
					</c:if> 
					<c:if test="${coordenadorTutorIMD.coordenador.id > 0 }">
						<h:commandButton value="Alterar" action="#{coordenadorTutorIMD.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{coordenadorTutorIMD.cancelarAlteracao}" onclick="#{confirm}"/>
					</c:if> 
				</td>
			</tr>
		</tfoot>
	 	<!-- Fim botões -->
	 	
	 	</table>
		
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>