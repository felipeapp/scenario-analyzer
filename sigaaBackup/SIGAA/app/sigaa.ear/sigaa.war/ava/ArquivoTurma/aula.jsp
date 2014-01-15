<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<%@include file="/portais/turma/menu_turma.jsp"%>
	<h2>Lançar Conteúdo das Aulas</h2>

	<table class="visualizacao">
<%--	<tr><td>
	<p>Caro docente, o formulário abaixo contém um cronograma de todas as suas aulas no semestre de acordo com o horário
	cadastrado para a turma. Especifique, para cada aula, o assunto e o conteúdo que será ministrado para os seus alunos
	visualizarem.</p></td></tr> --%>
	<tr><td>	

		<p align="center">
			Mostrar somente as aulas do mês:
			<h:selectOneMenu valueChangeListener="#{arquivosTurma.filtrarPorMes}" onchange="submit()">
				<f:selectItem itemLabel="TODOS" itemValue="0" />
				<f:selectItems value="#{arquivosTurma.meses}" />
			</h:selectOneMenu>
		</p>
		</td></tr></table>
		<br/>
		
		<t:dataTable var="conteudo" value="#{arquivosTurma.aulas}" styleClass="formulario" rowClasses="linhaPar,linhaImpar" align="center" width="97%">
			
			<f:facet name="caption">
				<h:outputText value="Conteúdo das Aulas"/>
			</f:facet>
	
			<t:column width="18%" style="text-align: right;" >
				<h:outputText value="#{conteudo.data}">
					<f:convertDateTime pattern="EE'-'dd'/'MMMM" />
				</h:outputText>
			</t:column>
			
			<t:column>
				<f:verbatim><b></f:verbatim><h:outputText value="#{conteudo.titulo}"/><f:verbatim></b></f:verbatim>
				<f:verbatim><br/></f:verbatim>
				<h:outputText value="#{conteudo.descricao}"/>
			</t:column>
			
			<t:column>
				<h:form>
				<f:verbatim><input type="hidden" name="id" value="</f:verbatim>
				<h:outputText value="#{ conteudo.id }"/>
				<f:verbatim>"/></f:verbatim>
				<h:commandButton value="Escolher" action="#{ arquivosTurma.selecionarArquivo }" />
				</h:form>
			</t:column>
			
		</t:dataTable>
		
		<br/>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
