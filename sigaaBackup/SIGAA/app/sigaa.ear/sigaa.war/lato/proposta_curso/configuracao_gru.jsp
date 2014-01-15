<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.dominio.ModalidadeEducacao"%>

<f:view>

	<h2>
		<ufrn:subSistema />
		&gt; Configura��o GRU
	</h2>

	<%@include file="include/_operacao.jsp"%>

	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Configura��o das Guias de Recolhimento da Uni�o - GRU</caption>
			<tbody>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{ cursoLatoMBean.possuiGRUInscricao }" id="possuiGRUInscricao"  
							onclick="submit()" onchange="submit()"/>
					</th>
					<td>O Curso usar� GRU para cobran�a de taxa de inscri��o em Processos Seletivos</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{ cursoLatoMBean.possuiGRUMensalidade }" id="possuiGRUMensalidade"  
							onclick="submit()" onchange="submit()"/>
					</th>
					<td>O Curso usar� GRU para pagamento de mensalidades</td>
				</tr>
				<!-- CONFIGURA��O DA GRU PARA PAGAMENTO DE TAXA DE INSCRI��O EM PROCESSOS SELETIVOS -->
				<h:panelGroup id="panelTaxaInscricao" rendered="#{ cursoLatoMBean.possuiGRUInscricao }">
					<tr>
						<td colspan="2" class="subFormulario">Taxa de Inscri��o</td>
					</tr>
					<tr>
						<th>Tipo de GRU:</th>
						<td><h:selectOneRadio id="tipoGRUInscricao" onchange="submit()" onclick="submit()"
								value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.gruSimples }">
								<f:selectItem itemValue="true" itemLabel="Simples" />
								<f:selectItem itemValue="false" itemLabel="Cobran�a" />
							</h:selectOneRadio></td>
					</tr>
					<tr>
						<th class="rotulo">Tipo de Arrecada��o:</th>
						<td>
							<h:outputText value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.tipoArrecadacao.descricao }" />
						</td>
					</tr>
					<tr>
						<th class="rotulo">C�digo de Recolhimento:</th>
						<td><h:outputText
								value="#{cursoLatoMBean.obj.configuracaoGRUInscricao.tipoArrecadacao.codigoRecolhimento.codigo} - #{cursoLatoMBean.obj.configuracaoGRUInscricao.tipoArrecadacao.codigoRecolhimento.descricao}"/>
						</td>
					</tr>
					<tr>
						<th class="rotulo">Unidade Favorecida:</th>
						<td><h:outputText value="#{ cursoLatoMBean.obj.unidadeOrcamentaria.nome }"/></td>
					</tr>
					<tr>
						<th class="obrigatorio">Grupo de Emiss�o da GRU:</th>
						<td>
							<h:selectOneMenu valueChangeListener="#{ cursoLatoMBean.grupoEmissaoListener }"
								value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id }"
								onchange="submit()"  id="grupoEmissaoGruInscricao">
								<f:selectItem itemValue="0"
									itemLabel="NOVO GRUPO DE EMISS�O DE GRU" />
								<f:selectItems value="#{cursoLatoMBean.gruposEmissaoGRU}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<c:set var="classeRotuloObrigatorio" value="${ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id == 0 ? 'obrigatorio' : 'rotulo' }" />
					<c:choose>
						<c:when test="${ cursoLatoMBean.obj.configuracaoGRUInscricao.gruSimples }">
							<tr>
								<th class="${ classeRotuloObrigatorio }">C�digo da Gest�o:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.codigoGestao }"
										size="10" maxlength="10" id="codigoGestaoGRUInscricao"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.codigoGestao }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
							<tr>
								<th class="${ classeRotuloObrigatorio }">C�digo da Unidade Gestora:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.codigoUnidadeGestora }"
										size="10" maxlength="10" id="codigoUnidadeGestoraGRUInscricao"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.codigoUnidadeGestora }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
						</c:when>
						<c:when test="${ !cursoLatoMBean.obj.configuracaoGRUInscricao.gruSimples }">
							<tr>
								<th class="${ classeRotuloObrigatorio }">Ag�ncia:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.agencia }"
										size="10" maxlength="10" id="agenciaGRUInscricao"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.agencia }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
							<tr>
								<th class="${ classeRotuloObrigatorio }">C�digo Cedente:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.codigoCedente }"
										size="10" maxlength="10" id="codigoCedenteGRUInscricao"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.codigoCedente }"
										id="codigoCedenteGRUInscricaoOut"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
							<tr>
								<th class="${ classeRotuloObrigatorio }">N�mero do Conv�nio:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.convenio }"
										size="10" maxlength="10" id="convenioGRUInscricao"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.convenio }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUInscricao.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
						</c:when>
					</c:choose>
				</h:panelGroup>
				<!-- CONFIGURA��O DA GRU PARA PAGAMENTO DE MENSALIDADES -->
				<h:panelGroup id="panelTaxaMensalidade" rendered="#{ cursoLatoMBean.possuiGRUMensalidade }">
					<tr>
						<td colspan="2" class="subFormulario">Pagamento de Mensalidade</td>
					</tr>
					<tr>
						<th>Tipo de GRU:</th>
						<td><h:selectOneRadio id="tipoGRUMensalidade" onchange="submit()" onclick="submit()"
								value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.gruSimples }">
								<f:selectItem itemValue="true" itemLabel="Simples" />
								<f:selectItem itemValue="false" itemLabel="Cobran�a" />
							</h:selectOneRadio></td>
					</tr>
					<tr>
						<th class="rotulo">Tipo de Arrecada��o:</th>
						<td>
							<h:outputText value="#{cursoLatoMBean.obj.configuracaoGRUMensalidade.tipoArrecadacao.descricao}" />
						</td>
					</tr>
					<tr>
						<th class="rotulo">C�digo de Recolhimento:</th>
						<td><h:outputText
								value="#{cursoLatoMBean.obj.configuracaoGRUMensalidade.tipoArrecadacao.codigoRecolhimento.codigo} - #{cursoLatoMBean.obj.configuracaoGRUMensalidade.tipoArrecadacao.codigoRecolhimento.descricao}"/>
						</td>
					</tr>
					<tr>
						<th class="rotulo">Unidade Favorecida:</th>
						<td><h:outputText value="#{ cursoLatoMBean.obj.unidadeOrcamentaria.nome}"/></td>
					</tr>
					<tr>
						<th class="obrigatorio">Grupo de Emiss�o da GRU:</th>
						<td>
							<h:selectOneMenu valueChangeListener="#{ cursoLatoMBean.grupoEmissaoListener }"
								value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id }"
								onchange="submit()"  id="grupoEmissaoGruMensalidade">
								<f:selectItem itemValue="0"
									itemLabel="NOVO GRUPO DE EMISS�O DE GRU" />
								<f:selectItems value="#{cursoLatoMBean.gruposEmissaoGRU}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<c:set var="classeRotuloObrigatorio" value="${ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id == 0 ? 'obrigatorio' : 'rotulo' }" />
					<c:choose>
						<c:when test="${ cursoLatoMBean.obj.configuracaoGRUMensalidade.gruSimples }">
							<tr>
								<th class="${ classeRotuloObrigatorio }">C�digo da Gest�o:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoGestao }"
										size="10" maxlength="10" id="codigoGestaoGRUMensalidade"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoGestao }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
							<tr>
								<th class="${ classeRotuloObrigatorio }">C�digo da Unidade Gestora:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoUnidadeGestora }"
										size="10" maxlength="10" id="codigoUnidadeGestoraGRUMensalidade"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoUnidadeGestora }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
						</c:when>
						<c:when test="${ !cursoLatoMBean.obj.configuracaoGRUMensalidade.gruSimples }">
							<tr>
								<th class="${ classeRotuloObrigatorio }">Ag�ncia:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.agencia }"
										size="10" maxlength="10" id="agenciaGRUMensalidade"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.agencia }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
							<tr>
								<th class="${ classeRotuloObrigatorio }">C�digo Cedente:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoCedente }"
										size="10" maxlength="10" id="codigoCedenteGRUMensalidade"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoCedente }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
							<tr>
								<th class="${ classeRotuloObrigatorio }">N�mero do Conv�nio:</th>
								<td><h:inputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.convenio }"
										size="10" maxlength="10" id="convenioGRUMensalidade"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id == 0 }" />
									<h:outputText
										value="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.convenio }"
										rendered="#{ cursoLatoMBean.obj.configuracaoGRUMensalidade.grupoEmissaoGRU.id != 0 }" />
								</td>
							</tr>
						</c:when>
					</c:choose>
				</h:panelGroup>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{cursoLatoMBean.telaAnterior}" id="voltar" />
						<h:commandButton value="Cancelar" action="#{cursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
						<h:commandButton value="Avan�ar >>" action="#{cursoLatoMBean.cadastrar}" id="cadastrar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento
			obrigat�rio. </span>
	</center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>