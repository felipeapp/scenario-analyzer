<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/discente/menu_discente.jsp"%>

<h2><ufrn:subSistema /> > Relat�rio de Discente de Extens�o</h2>


	<h:form id="formRelatorioBolsistaExtensao">
		<h:messages showDetail="true"></h:messages>
		<h:inputHidden value="#{relatorioBolsistaExtensao.confirmButton}" />
		<input type="hidden" value="${relatorioBolsistaExtensao.obj.id}" name="id" id="id"/>
		
		<table class="formulario" width="100%" >
			<caption class="listagem">Relat�rio de Discente de Extens�o</caption>

			<tr>
				<td colspan="2">A��o de Extens�o: <br /> 
				<b><h:outputText id="titulo_acao"	value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.atividade.codigoTitulo }"/></b>
				</td>
			</tr>

			<tr>
				<td colspan="2">Tipo de Relat�rio: <br /> 
				<b><h:outputText id="tipo_relatorio"	value="#{ relatorioBolsistaExtensao.obj.tipoRelatorio.descricao}"/></b>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">Discente: <br /> 
				<b><h:outputText value="#{ relatorioBolsistaExtensao.obj.discenteExtensao.discente.matriculaNome }" id="discente"/></b>
				</td>
			</tr>

			
			<tr>
				<td colspan="2">Introdu��o: <br />
					<h:inputTextarea value="#{relatorioBolsistaExtensao.obj.introducao}" rows="3" style="width:98%" 
						readonly="#{relatorioBolsistaExtensao.readOnly}" id="introducao" />
				</td>
			</tr>

			<tr>
				<td colspan="2">Metodologia: 
					<br /> 
					<h:inputTextarea
					value="#{relatorioBolsistaExtensao.obj.metodologia}" style="width:98%" rows="3"
					readonly="#{relatorioBolsistaExtensao.readOnly}" id="metodologia" />
				</td>
			</tr>

			<tr>
				<td colspan="2">Atividades Realizadas: 
					<ufrn:help img="/img/ajuda.gif">Descrever detalhadamente as A��es desenvolvidas no projeto, anexando c�pia, quando poss�vel,  de trabalhos publicados e/ou apresentados em eventos de car�ter t�cnico ou cient�fico, etc.</ufrn:help>
					<br />
					<h:inputTextarea value="#{relatorioBolsistaExtensao.obj.atividades}" style="width:98%" rows="3"
					readonly="#{relatorioBolsistaExtensao.readOnly}" id="atividades" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2">Resultados: 
					<ufrn:help img="/img/ajuda.gif">Sintetizar a relev�ncia acad�mica e social do projeto, a articula��o com o ensino e a pesquisa e o retorno ao respectivo Curso ou �rea de conhecimento, o cumprimento das metas, aspectos positivos, avalia��o geral, etc.</ufrn:help>
					<br />
					<h:inputTextarea value="#{relatorioBolsistaExtensao.obj.resultados}" style="width:98%" rows="3"
					readonly="#{relatorioBolsistaExtensao.readOnly}" id="resultados" />
				</td>
			</tr>			
			
			<tr>
				<td colspan="2">Conclus�es: 
					<ufrn:help img="/img/ajuda.gif">Indicar se houve altera��o na proposta inicial, as dificuldades encontradas e maneira como foram solucionadas, contribui��o para a forma��o profissional do aluno, perspectivas para trabalhos futuros, etc.</ufrn:help>
					<br />
					<h:inputTextarea value="#{relatorioBolsistaExtensao.obj.conclusoes}" style="width:98%" rows="3"
					readonly="#{relatorioBolsistaExtensao.readOnly}" id="conclusoes" />
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{relatorioBolsistaExtensao.confirmButton}"
							action="#{relatorioBolsistaExtensao.cadastrar}" rendered="#{relatorioBolsistaExtensao.confirmButton != 'Remover'}"/> 
						<h:commandButton value="#{relatorioBolsistaExtensao.confirmButton}"
							action="#{relatorioBolsistaExtensao.inativar}" rendered="#{relatorioBolsistaExtensao.confirmButton == 'Remover'}"/>
						<h:commandButton value="Cancelar" action="#{relatorioBolsistaExtensao.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>