<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > Avaliação de Relatório de Discente de Extensão</h2>


	<h:form id="formRelatorioBolsistaExtensao">
		<h:messages showDetail="true"></h:messages>
		<h:inputHidden value="#{relatorioBolsistaExtensao.confirmButton}" />
		
		<table class="formulario" width="100%" >
			<caption class="listagem">Avaliação de Relatório de Discente de Extensão</caption>

			<tr>
				<td colspan="2"><b>Ação de Extensão:</b> <br /> 
				<h:outputText id="tituloatividade"	value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.atividade.codigoTitulo }"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><b>Discente:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.discente.matriculaNome }" id="discente"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><b>Tipo de Vínculo:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.tipoVinculo.descricao }" id="vinculo"/>
				</td>
			</tr>			
			

			<tr>
				<td colspan="2"><b>Curso do Discente:</b> <br /> 
				<h:outputText value="#{relatorioBolsistaExtensao.obj.discenteExtensao.discente.curso.descricao}" id="curso"/>
				</td>
			</tr>

			<tr>
				<td class="subFormulario">Dados do Relatório</td>
			</tr>


			<tr>
				<td colspan="2"><b>Tipo:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.tipoRelatorio.descricao }" id="tipoRelatorio"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Data de Envio:</b> <br /> 
				<h:outputText value="#{ relatorioBolsistaExtensao.obj.dataEnvio }" id="dataEnvio"/>
				</td>
			</tr>

			
			<tr>
				<td colspan="2"><b>Introdução:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.introducao}" style="width:98%" id="introducao" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Metodologia:</b> <br /> 
					<h:outputText	value="#{relatorioBolsistaExtensao.obj.metodologia}" 	id="metodologia" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Ações:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.atividades}" id="atividades" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><b>Resultados:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.resultados}" id="resultados" />
				</td>
			</tr>			
			
			<tr>
				<td colspan="2"><b>Conclusões:</b> <br />
					<h:outputText value="#{relatorioBolsistaExtensao.obj.conclusoes}" id="conclusoes" />
					<br/>
					<br/>					
				</td>
			</tr>

			<tr>
				<td class="subFormulario">Parecer da Coordenação da Ação Sobre o Relatório do(a) Discente</td>
			</tr>
			
			<tr>
				<td></b>Detalhes do Parecer:</b><br/>
				<h:inputTextarea value="#{relatorioBolsistaExtensao.obj.parecerOrientador}" id="parecerOrientador" rows="3" style="width: 98%" /></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar Parecer"
							action="#{relatorioBolsistaExtensao.validarRelatorio}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioBolsistaExtensao.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>