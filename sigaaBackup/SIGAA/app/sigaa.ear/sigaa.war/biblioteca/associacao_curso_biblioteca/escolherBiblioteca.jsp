<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="associacaoCursoBibliotecaMBean" />
	
	<c:set var="mbean" value="#{ associacaoCursoBibliotecaMBean }" />
	
	<h2> <ufrn:subSistema /> &gt; Cursos Associados </h2>

	<h:form>
	
		<div class="descricaoOperacao">
			<p>Essa operação permite associar os cursos existentes na instituição às Biblioteca Setoriais do Sistema.</p>
			<p>Com essa associação é possível definir quais biblioteca atenderão os alunos de quais cursos com relação aos serviços prestados por elas.</p>
			<p>Aqueles cursos que não estiverem associados a nenhuma biblioteca, serão atendidos pela Biblioteca Central.</p>
			<br/>
			<p>Escolha uma das Bibliotecas Setoriais abaixo listadas.</p>
		</div>
		
		<table class="formulario">
		
			<caption>Escolha uma biblioteca</caption>
		
			<tr>
				<th class="obrigatorio">Biblioteca:</th>
				<td>
					<h:selectOneMenu value="#{ mbean.biblioteca.id }" id="biblioteca" >
						<f:selectItem itemLabel="-- Selecione uma Biblioteca --" itemValue="-1"  escape="false"/>
						<f:selectItems value="#{ mbean.bibliotecasSetoriaisCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{ mbean.cancelar }" id="voltar" onclick="#{confirm}" immediate="true" />
						<h:commandButton value="Continuar >> " action="#{ mbean.listar }"   id="continuar" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>