<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="convenioEstagioMBean" />
	<a4j:keepAlive beanName="statusConvenioEstagioMBean"/>
	<h2> <ufrn:subSistema /> &gt; Análise de Convênio de Estágio</h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usuário, </b>
		</p> 	
		<p>
			Você deverá informar a nova situação referente ao parecer da solicitação de Convênio de Estágio selecionada.
		</p>
	</div>
	
	<table class="visualizacao" style="width: 80%">
		<caption>Dados da Solicitação de Convênio de Estágio</caption>
		<tr>
			<th width="200px;" valign="top">Tipo do Convênio:</th>
			<td>
				<h:outputText value="#{convenioEstagioMBean.obj.tipoConvenio.descricao}"/>
			</td>
		</tr>	
		<tr>
			<th>Concedente de Estágio:</th>
			<td>
				<h:outputText value="#{convenioEstagioMBean.obj.nomeCNPJEmpresa}"/>
			</td>
		</tr>
		<tr>
			<th>Responsável:</th>
			<td>
				<h:outputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.nome}"/>																																				
			</td>
		</tr>
		<tr>
			<th>Situação Atual:</th>
			<td>
				<h:outputText value="#{convenioEstagioMBean.status.descricao}"/>																																			
				<h:outputText value="NÃO DEFINIDO" rendered="#{empty convenioEstagioMBean.status.descricao}"/>
			</td>
		</tr>
		<c:if test="${convenioEstagioMBean.obj.idArquivoTermoConvenio != null}">
			<tr>
				<th>Termo de Convênio Digitalizado:</th>
				<td>
					<a href="${ctx}/verArquivo?idArquivo=${convenioEstagioMBean.obj.idArquivoTermoConvenio}&key=${ sf:generateArquivoKey(convenioEstagioMBean.obj.idArquivoTermoConvenio) }" target="_blank" id="arquivoDigitalizadoAtual">
						clique arqui para visualizar
						<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
					</a>																																				
				</td>
			</tr>
		</c:if>
	</table>	
	<br/>
	<h:form enctype="multipart/form-data" id="form">
		<table class="formulario" width="80%">
			<caption>Informe o Novo Status do Convênio</caption>					
			<tr>
				<th width="35%" class="obrigatorio">Situação:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu id="situacao" value="#{convenioEstagioMBean.obj.status.id}" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{statusConvenioEstagioMBean.statusConvenioEspecificosCombo}"/>
							<a4j:support event="onchange" reRender="form"/>
						</h:selectOneMenu>
					</a4j:region>				
				</td>			
			</tr>
			<a4j:region rendered="#{convenioEstagioMBean.obj.aprovado}">
				<tr>
					<th>PDF Digitalizado do Termo de Convênio:</th>
					<td>
						<t:inputFileUpload value="#{convenioEstagioMBean.arquivoTermoConvenio}" styleClass="file" id="nomeArquivo" />
						<ufrn:help>O usuário deverá digitalizar o Termo de Convênio que foi assinado por todas as partes e enviar.</ufrn:help>
					</td>
				</tr>				
				<tr>
					<th class="obrigatorio">Número do Convênio:</th>
					<td>
						<h:inputText value="#{convenioEstagioMBean.obj.numeroConvenio}" id="numeroConvenio" size="10" maxlength="30"/>						
					</td>					
				</tr>	
				<a4j:region rendered="#{convenioEstagioMBean.obj.agenteInterno}">
					<tr>
						<th>Unidade:</th>
						<td>
					        <h:inputText value="#{convenioEstagioMBean.obj.concedente.unidade.nome}" id="inputTextUnidade"  size="60" maxlength="70" />
							
							<rich:suggestionbox id="suggestionBoxUnidade" for="inputTextUnidade" tokens=",."
					                   suggestionAction="#{unidade.autocompleteUnidades}" var="_result"
					                   fetchValue="#{_result.nome}"
					                   minChars="5"
					                   shadowOpacity="false"
					                   width="500" height="300"
					                   shadowDepth="false"
					                   cellpadding="3px"
					                   nothingLabel="UNIDADE NÃO ENCONTRADA"
					                   usingSuggestObjects="true">
					                
					                	<%-- Elementos que vão ser rederizados no menu com os resulatados da busca --%>
					                   <h:column>
					                       <h:outputText value="#{_result.codigoFormatado}  #{_result.nome}" />
					                   </h:column>
					                   
					                   <a4j:support event="onselect">
											<f:setPropertyActionListener value="#{_result.id}" target="#{convenioEstagioMBean.obj.concedente.unidade.id}" />
									  </a4j:support>									  				                                     
							</rich:suggestionbox>  
						</td>
					</tr>
					<tr>
						<th>Código do Projeto:</th>
						<td>
							<h:inputText value="#{convenioEstagioMBean.obj.concedente.codigoProjeto}" id="codigoProjeto" size="10" maxlength="10" 
								onkeyup="return formatarInteiro(this);"/> 							
						</td>					
					</tr>	
				</a4j:region>						
			</a4j:region>
			<a4j:region rendered="#{convenioEstagioMBean.obj.recusado}">
				<tr>
					<th class="obrigatorio">Motivo da Análise:</th>
					<td>
						<h:inputTextarea value="#{convenioEstagioMBean.obj.motivoAnalise}" id="motivo" cols="80" rows="3"/>						
					</td>					
				</tr>				
			</a4j:region>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btConfirmar" value="Confirmar" action="#{convenioEstagioMBean.cadastrar}"/>
						<h:commandButton id="btVoltar" value="<< Voltar" action="#{convenioEstagioMBean.telaConsulta}" rendered="#{ !convenioEstagioMBean.cadastro }"/>
						<h:commandButton id="btVoltar2" value="<< Voltar" action="#{convenioEstagioMBean.telaForm}" rendered="#{ convenioEstagioMBean.cadastro }"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{convenioEstagioMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>							
		</table>		
	</h:form>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	