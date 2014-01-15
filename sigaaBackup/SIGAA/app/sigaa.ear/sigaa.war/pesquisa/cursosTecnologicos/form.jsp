<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
 	<a4j:keepAlive beanName="cursosTecnologicosMBean" />
	<h2><ufrn:subSistema /> &gt; Cursos Tecnológicos</h2>

 <h:form id="form">
 
	<center>
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
  			<h:commandLink value="Listar os Cursos Cadastrados" action="#{cursosTecnologicosMBean.listar}"/>
		</div>
	</center>

	<table class=formulario width="64%">
			<caption class="listagem">Cursos Tecnológicos</caption>
			<h:inputHidden value="#{cursosTecnologicosMBean.confirmButton}" />
			<h:inputHidden value="#{cursosTecnologicosMBean.obj.id}" />

			<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
					<h:inputHidden id="idCurso" value="#{ cursosTecnologicosMBean.obj.curso.id }" />
					<h:inputText id="nomeCurso" value="#{ cursosTecnologicosMBean.obj.curso.nome }" size="80" maxlength="120" style="width: 500px;" />
					<rich:suggestionbox id="suggestionNomeCurso" for="form:nomeCurso" var="_curso" nothingLabel="Nenhum curso encontrado" 
							suggestionAction="#{ cursosTecnologicosMBean.autocompleteNomeGeralCursos }" width="500" height="250" minChars="3" >
						<h:column>
							<h:outputText value="#{ _curso.descricao }"/> - 
							( <h:outputText value="#{ _curso.municipio }" /> )
						</h:column>
						
						<a4j:support event="onselect" reRender="form" >
							<f:setPropertyActionListener value="#{ _curso.id }" target="#{ cursosTecnologicosMBean.obj.curso.id }"/>
						</a4j:support>
						
					</rich:suggestionbox>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnCadastrar" value="#{cursosTecnologicosMBean.confirmButton}" action="#{cursosTecnologicosMBean.cadastrar}" /> 
						<h:commandButton id="btnCancelar" immediate="true" value="Cancelar" onclick="#{confirm}" action="#{cursosTecnologicosMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
	</table>

	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
 </h:form>
 
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>