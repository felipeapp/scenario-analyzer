<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="associacaoCursoBibliotecaMBean" />
	
	<c:set var="mbean" value="#{ associacaoCursoBibliotecaMBean }" />
	
	<h2> <ufrn:subSistema /> &gt; Cursos Associados </h2>

	<h:form>
	
		<div class="descricaoOperacao">
			<p>Essa opera��o permite associar os cursos existentes na institui��o �s Biblioteca Setoriais do Sistema.</p>
			<p>Com essa associa��o � poss�vel definir quais biblioteca atender�o os alunos de quais cursos com rela��o aos servi�os prestados por elas.</p>
			<p>Aqueles cursos que n�o estiverem associados a nenhuma biblioteca, ser�o atendidos pela Biblioteca Central.</p>
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