<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h2>Relat�rio de Discente de Extens�o</h2>

	<h:form id="formRelatorioBolsistaExtensao">
		<h:messages showDetail="true"></h:messages>
		<h:inputHidden value="#{relatorioBolsistaExtensao.confirmButton}" />

		
		<h3 class="tituloTabelaRelatorio"> Relat�rio de Discente de Extens�o </h3>
		<table class="tabelaRelatorio"  width="100%" >
			<tr>
				<td colspan="2"><b>A��o de Extens�o:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.atividade.codigoTitulo }" id="acao"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Discente:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.discente.matriculaNome }" id="discente"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Tipo de V�nculo:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.tipoVinculo.descricao }" id="vinculo"/>
				</td>
			</tr>			

			<tr>
				<td colspan="2"><b>Curso do Discente:</b> <br /> 
					<h:outputText value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.discente.curso.descricao }" id="curso"/>
					<br />
				</td>
			</tr>

			<tr>
				<td class="infoRelatorio"><b>Dados do Relat�rio</b></td>
			</tr>


			<tr>
				<td colspan="2"><b>Tipo:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.tipoRelatorio.descricao }" id="tipoRelatorio"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Data de Envio:</b> <br /> 
					<fmt:formatDate value="${relatorioBolsistaExtensao.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Introdu��o:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.introducao}" style="width:98%" id="introducao" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Metodologia:</b> <br /> 
					<h:outputText	value="#{relatorioBolsistaExtensao.obj.metodologia}" 	id="metodologia" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Atividades Realizadas:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.atividades}" id="atividades" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><b>Resultados:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.resultados}" id="resultados" />
				</td>
			</tr>			
			
			<tr>
				<td colspan="2"><b>Conclus�es:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.conclusoes}" id="conclusoes" />
					<br/>
					<br/>					
				</td>
			</tr>
			
			
			<tr>
				<td colspan="2" class="infoRelatorio"><b>Parecer da Coordena��o da A��o Sobre o Relat�rio do(a) Discente</b></td>
			</tr>
			
			<c:if test="${ relatorioBolsistaExtensao.obj.dataParecer != null}">			
				<tr>
					<td colspan="2"><b>Data do Parecer:</b> <br /> 
						<fmt:formatDate value="${relatorioBolsistaExtensao.obj.dataParecer}" pattern="dd/MM/yyyy HH:mm:ss"/>
					</td>
				</tr>
				
				<tr>
					<td colspan="2"><b>Parecer:</b> <br /> 
					<h:outputText value="#{ relatorioBolsistaExtensao.obj.parecerOrientador }" id="parecer"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${ relatorioBolsistaExtensao.obj.dataParecer == null}">
				<tr>
					<td colspan="2"><center><font color="red">Relat�rio est� sendo analisado pelo(a) Coordenador(a) da A��o de Extens�o</font></center></td>
				</tr>
			</c:if>
			
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>