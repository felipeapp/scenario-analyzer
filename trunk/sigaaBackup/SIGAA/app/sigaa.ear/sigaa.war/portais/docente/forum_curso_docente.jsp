<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema/> > Vincular Servidor a Fórum de Curso</h2>
	<a4j:keepAlive beanName="forumCursoDocente"></a4j:keepAlive>
	
	<div class="descricaoOperacao">
		<p><b>Caso Usuário,</b></p>
		<br/>
		<p>Nesta tela será possível conceder acesso ao <b>Docente</b> a um determinado <b>Fórum de Curso</b>.</p>
	</div>
	
	<h:form id="form">
		<table class="formulario" width="70%">
			<caption>Selecione um Docente</caption>
			<tr>
				<th class="obrigatorio">Docente:</th>
				<td>
					<a4j:region id="docente">
						<h:inputText value="#{forumCursoDocente.servidor.pessoa.nome}" id="nomeDocente" style="width: 400px;"/>
						<rich:suggestionbox width="400" height="100" for="nomeDocente" id="sbDocente"
							minChars="1" nothingLabel="#{servidor.textoSuggestionBox}"
							suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}"
							onsubmit="$('form:imgStDocente').style.display='inline';" 
						    oncomplete="$('form:imgStDocente').style.display='none';">

							<h:column>
								<h:outputText value="#{_servidor.siape}"/>
							</h:column>

							<h:column>
								<h:outputText value="#{_servidor.nome}"/>
							</h:column>
							
		                   <a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_servidor.id}" target="#{forumCursoDocente.servidor.id}" />
						  </a4j:support>								
							
						</rich:suggestionbox>
						<h:graphicImage id="imgStDocente" style="display:none; overflow: visible;" value="/img/indicator.gif"/>	
					</a4j:region>					
				</td>
			</tr>	
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="cancelar" value="Cancelar" action="#{forumCursoDocente.cancelar}" onclick="#{confirm}" immediate="true"/> 
						<h:commandButton id="buscar" value="Continuar >>" action="#{forumCursoDocente.buscaForunsDocente}" />
					</td>
				</tr>
			</tfoot>		
		</table>
		
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>