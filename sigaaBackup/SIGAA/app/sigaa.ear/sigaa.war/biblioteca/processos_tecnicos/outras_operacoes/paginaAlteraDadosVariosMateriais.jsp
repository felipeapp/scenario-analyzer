<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2>  <ufrn:subSistema /> &gt; Editar Informações Materiais</h2>

<style type="text/css">
	
	.codigoBarras{
		width: 30%;
	}
	
	.campoEdicao{
		width: 69%;
	}

	.replicar{
		width: 1%;
	}

</style>

<f:view>

	<a4j:outputPanel ajaxRendered="true" >

		<h:form id="formAlteraDadosVariosMateriais">
		
			<a4j:keepAlive beanName="alteraDadosVariosMateriaisMBean"></a4j:keepAlive> 
	
			<%-- Caso o usuário deseja voltar para a tela de pesquisa --%>
			<%-- <a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive> --%>
			
			<%-- Caso o usuário deseja voltar para a tela de pesquisa --%>
			<a4j:keepAlive beanName="pesquisaMateriaisInformacionaisMBean"></a4j:keepAlive>
	
			<%-- <c:set var="_titulo" value="${alteraDadosVariosMateriaisMBean.titulo}"/>
			<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%> --%>
						
			<table class="formulario" width="80%">
				<caption> Títulos Selecionados ( ${fn:length(alteraDadosVariosMateriaisMBean.titulosMateriais) } )</caption>
							
				<thead>		
					<tr><th style="text-align: left; width: 30%">Informações</th></tr>
				</thead>
				
				<tbody>
					<c:forEach items="${alteraDadosVariosMateriaisMBean.titulosMateriais}" var="tituloMaterial" varStatus="row">
						<tr class="${row.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${tituloMaterial}</td>
						</tr> 
					</c:forEach>
				</tbody>
			</table>
					
	
	
			<div class="infoAltRem" style="margin-top: 10px; width: 80%">
	
				<h:graphicImage value="/img/arrow_down.png" style="overflow: visible;" />: 
				Copiar o valor do campo para os demais campos abaixo dele
			</div>
	
	
	
			<table class="formulario" width="80%">
	
				<caption> Materiais Selecionados ( ${fn:length(alteraDadosVariosMateriaisMBean.materiaisParaAlteracao) } )</caption>
							
				<thead>
					
					<th style="text-align: left; width: 30%">Código de Barras do Material</th>
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarNumeroChamada}"> 
						<th style="text-align: left">Número de Chamada</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarSegundaLocalizacao}"> 
						<th style="text-align: left">Segunda Localização</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaGeral}"> 
						<th style="text-align: left">Nota Geral</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaUsuario}"> 
						 <th style="text-align: left">Nota Usuário</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarColecao}"> 
						<th style="text-align: left">Coleção</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarSituacao}"> 
						<th style="text-align: left">Situação</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarStatus}"> 
						<th style="text-align: left">Status</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarTipoMaterial}"> 
						<th style="text-align: left">Tipo de Material</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaTeseDissertacao}"> 
						<th style="text-align: left">Alterar Nota de Tese e Dissertação</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaConteudo}"> 
						<th style="text-align: left">Nota de Conteúdo</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarNumeroVolume}"> 
						<th style="text-align: left">Número do Volume</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarAnoCronologico}"> 
						<th style="text-align: left">Ano Cronológico</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarAno}"> 
						<th style="text-align: left">Ano</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarVolume}"> 
						<th style="text-align: left">Volume</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarNumero}"> 
						<th style="text-align: left">Número</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarEdicao}"> 
						<th style="text-align: left">Edição</th>
					</c:if> 
					
					<c:if test="${alteraDadosVariosMateriaisMBean.alterarDescricaoSuplemento}"> 
						<th style="text-align: left">Descrição do Suplemento</th>
					</c:if> 
					
					<th style="width: 1%"> </th>
					
				</thead>
			</table>
	
	
	
	
	
			<t:dataTable var="material" rowIndexVar="index" value="#{alteraDadosVariosMateriaisMBean.materiaisParaAlteracao}" 
			  	columnClasses="codigoBarras, campoEdicao, replicar" rowClasses="linhaPar, linhaImpar" style="width:80%; margin-left:auto; margin-right:auto; border:1px solid #DEDFE3; ">
				
				<t:column>
					<h:outputText id="outputNumeroChamada" value="#{material.codigoBarras}" />
				</t:column>
				
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarNumeroChamada}"> 
					<t:column> <h:inputText id="inputNumeroChamada" value="#{material.numeroChamada}"  disabled="#{material.situacao.situacaoDeBaixa}" size="50" maxlength="200"/></t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarSegundaLocalizacao}"> 
					<t:column> <h:inputText id="inputSegundaLocalizacao" value="#{material.segundaLocalizacao}" disabled="#{material.situacao.situacaoDeBaixa}"  size="50" maxlength="200"/></t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaGeral}"> 
					<t:column>
						<h:inputTextarea id="inputAreaNotaGeral" value="#{ material.notaGeral }" 	disabled="#{material.situacao.situacaoDeBaixa}" cols="57" rows="2"  />
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaUsuario}"> 
					<t:column>
						<h:inputTextarea id="inputAreaNotaUsuario" value="#{ material.notaUsuario }" disabled="#{material.situacao.situacaoDeBaixa}" cols="57" rows="2"  />
						<ufrn:help> Informações que vão aparecer para o usuário nas consultas públicas do sistema</ufrn:help>
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarColecao}"> 
					<t:column>
						<h:selectOneMenu id="comboColecao" value="#{alteraDadosVariosMateriaisMBean.valoresComboBox[index]}" disabled="#{material.situacao.situacaoDeBaixa}">
							<f:selectItems value="#{alteraDadosVariosMateriaisMBean.colecoes}" />
						</h:selectOneMenu>
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarSituacao}"> 
					
					<t:column rendered="#{material.situacao.situacaoEmprestado}">
						<h:selectOneMenu id="comboSituacaoEmpretado" disabled="#{material.situacao.situacaoDeBaixa || material.situacao.situacaoEmprestado}">
							<f:selectItem itemLabel=" Emprestado " itemValue="#{alteraDadosVariosMateriaisMBean.idSituacaoEmprestado}"/>
						</h:selectOneMenu>
					</t:column>
					
					<t:column rendered="#{! material.situacao.situacaoEmprestado}">
						<h:selectOneMenu id="comboSituacao" value="#{alteraDadosVariosMateriaisMBean.valoresComboBox[index]}" disabled="#{material.situacao.situacaoDeBaixa || material.situacao.situacaoEmprestado}">
							<f:selectItems value="#{alteraDadosVariosMateriaisMBean.situacoes}"/>
						</h:selectOneMenu>
					</t:column>
					
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarStatus}"> 
					<t:column>
						<h:selectOneMenu id="comboStatus" value="#{alteraDadosVariosMateriaisMBean.valoresComboBox[index]}" disabled="#{ material.situacao.situacaoDeBaixa}">
							<f:selectItems value="#{alteraDadosVariosMateriaisMBean.statusAtivos}"/>
						</h:selectOneMenu>
					</t:column>
				</c:if>
				
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarTipoMaterial}"> 
					<t:column>
						<h:selectOneMenu id="comboTipoMaterial" value="#{alteraDadosVariosMateriaisMBean.valoresComboBox[index]}" disabled="#{ material.situacao.situacaoDeBaixa}">
							<f:selectItems  value="#{alteraDadosVariosMateriaisMBean.tiposMaterial}" />
						</h:selectOneMenu>
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaTeseDissertacao}"> 
					<t:column>
						<h:inputTextarea id="inputAreaNotaTeseDiscentacao" value="#{ material.notaTeseDissertacao }" disabled="#{material.situacao.situacaoDeBaixa}" cols="57" rows="2" />
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarNotaConteudo}"> 
					<t:column>
						<h:inputTextarea id="inputAreaNotaConteudo" value="#{ material.notaConteudo }" disabled="#{material.situacao.situacaoDeBaixa}" cols="57" rows="2" />
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarNumeroVolume}"> 
					<t:column>
						<h:inputText id="inputNumeroVolume" value="#{ material.numeroVolume}"  size="7" maxlength="6" onkeyup="return formatarInteiro(this);" disabled="#{ material.situacao.situacaoDeBaixa}"/>
					</t:column>
				</c:if> 
				
				
				<%-- Dados dos fascículos --%>
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarAnoCronologico}"> 
					<t:column>
						<h:inputText id="inputAnoCronologico" value="#{material.anoCronologico}" maxlength="20"  size="10" disabled="#{material.situacao.situacaoDeBaixa}"/>
						<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um ano cronológico. Exemplo: 2009-2010</ufrn:help>
					</t:column>
					
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarAno}"> 
					<t:column>
						<h:inputText id="inputAno" value="#{material.ano}" maxlength="20"  size="10" disabled="#{material.situacao.situacaoDeBaixa}"/>
						<ufrn:help>Número que faz referência ao ano de criação do fascículo. Esse campo pode conter letras para fascículos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarVolume}"> 
					<t:column>
						<h:inputText id="inputVolume" value="#{material.volume}" maxlength="20"  size="10" disabled="#{material.situacao.situacaoDeBaixa}"/>
						<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarNumero}"> 
					<t:column>
						<h:inputText id="inputNumero" value="#{material.numero}" maxlength="10"  size="10" disabled="#{material.situacao.situacaoDeBaixa}"/>
						<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um número. Exemplo: 10-20</ufrn:help>
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarEdicao}"> 
					<t:column>
						<h:inputText id="inputTextEdicao" value="#{material.edicao}" maxlength="20" size="10" disabled="#{material.situacao.situacaoDeBaixa}"/>
						<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de uma edição. Exemplo: 10-20</ufrn:help>
					</t:column>
				</c:if> 
				
				<c:if test="${alteraDadosVariosMateriaisMBean.alterarDescricaoSuplemento}"> 
					<t:column>
						<h:inputTextarea id="inputDescricaoSuplemento" value="#{ material.descricaoSuplemento }" cols="57" rows="1"  disabled="#{material.situacao.situacaoDeBaixa}"/>
					</t:column>
				</c:if> 

				<t:column rendered="#{! alteraDadosVariosMateriaisMBean.alterarSituacao || ( alteraDadosVariosMateriaisMBean.alterarSituacao && ! material.situacao.situacaoDeBaixa && ! material.situacao.situacaoEmprestado ) }">
					<a4j:commandLink actionListener="#{alteraDadosVariosMateriaisMBean.copiaValorCamposAbaixo}" reRender="formAlteraDadosVariosMateriais">
						<h:graphicImage url="/img/arrow_down.png" style="border:none"
							title="Clique aqui copiar o valor deste campo para os demais campos abaixo" />

						<f:param name="idMaterialOrigemDado" value="#{material.id}"/>					
					</a4j:commandLink>
				</t:column>
			
			</t:dataTable>
	
	
	
	
	
	
	
			<table class="formulario" width="80%">
				
				<tfoot>
					<tr>				
						<td colspan="9" style="text-align: center;">
							<h:commandButton value="Finalizar Alteração"  action="#{alteraDadosVariosMateriaisMBean.realizarAlteracaoMateriais}">
								 <f:setPropertyActionListener target="#{alteraDadosVariosMateriaisMBean.finalizarAlteracao}" value="true" />
							</h:commandButton>
							
							<h:commandButton value="Salvar"  action="#{alteraDadosVariosMateriaisMBean.realizarAlteracaoMateriais}">
								<f:setPropertyActionListener target="#{alteraDadosVariosMateriaisMBean.finalizarAlteracao}" value="false" />
							</h:commandButton>
							
							<h:commandButton value="<< Voltar"  action="#{alteraDadosVariosMateriaisMBean.telaPaginaSelecionaCamposDosMateriaisAlteracao}"/>
							<h:commandButton value="Cancelar"  action="#{alteraDadosVariosMateriaisMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
				
			</table>
	
	
	

		
	
		</h:form>
		
	</a4j:outputPanel>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>