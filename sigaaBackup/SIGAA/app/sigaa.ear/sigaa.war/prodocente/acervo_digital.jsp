<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2> <ufrn:subSistema/> > Acervo Digital de Produções </h2>

<h:form id="formBusca">

	<table>
		<tr>
			<td width="40">
				<img src="${ctx}/img/prodocente/acervo_digital.jpg" width="100" height="80">
			</td>
			<td align="center">
				Caros Usuários, neste espaço é possível consultar as produções intelectuais disponibilizadas
				pelos docentes no SIGAA. Este espaço pode ser utilizado como uma ferramenta de estudo, pesquisa tanto
				pelos docentes como pelos alunos. <br>

				Docente, disponibilize sua produção também para aumentar esta base.<br>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<br>
				<h:messages/>
				<table class="formulario">
					<caption> Busca de Produções </caption>
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{acervoProducao.checkTitulo}" id="checkBoxTitulo" styleClass="noborder"/>
						</td>
						<td>Título:</td>
						<td> <h:inputText id="titulo" value="#{acervoProducao.titulo}" size="40" onchange="javascript:$('formBusca:checkBoxTitulo').checked = true;"/> </td>
					</tr>
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{acervoProducao.checkTipoProducao}" id="checkBoxTipoProducao" styleClass="noborder"/>
						</td>
						<td>Tipo de Produção:</td>
						<td>
							<h:selectOneMenu id="tipo" value="#{acervoProducao.tipoProducao}" onchange="javascript:$('formBusca:checkBoxTipoProducao').checked = true;">
								<f:selectItem itemLabel="--SELECIONE--" itemValue="-1"/>
								<f:selectItems value="#{acervoProducao.tipoProducaoCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{acervoProducao.checkAreaConhecimento}" id="checkBoxAreaConhecimento" styleClass="noborder"/>
						</td>					
						<td>Area de conhecimento:</td>
						<td>
							<h:selectOneMenu id="area" value="#{acervoProducao.areaConhecimento}" onchange="javascript:$('formBusca:checkBoxAreaConhecimento').checked = true;">
								<f:selectItem itemLabel="--SELECIONE--" itemValue="-1"/>
								<f:selectItems value="#{acervoProducao.areaConhecimentoCombo}" />
							</h:selectOneMenu>							
						 </td>
					</tr>
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{acervoProducao.checkAnoPublicacao}" id="checkBoxAnoPublicacao" styleClass="noborder"/>
						</td>						
						<td>Ano de publicação:</td>
						<td> <h:inputText id="ano" value="#{acervoProducao.anoPublicacao}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onchange="javascript:$('formBusca:checkBoxAnoPublicacao').checked = true;" /> </td>
					</tr>						
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{acervoProducao.checkDepartamento}" id="checkBoxDepartamento" styleClass="noborder"/>
						</td>											
						<td> Departamento: </td>
						<td>
							<h:selectOneMenu id="departamento" value="#{acervoProducao.departamento}" onchange="javascript:$('formBusca:checkBoxDepartamento').checked = true;">
								<f:selectItem itemLabel="--SELECIONE--" itemValue="-1"/>
								<f:selectItems value="#{unidade.allDepartamentoCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton id="btnBuscar" actionListener="#{acervoProducao.buscar}" value="Buscar"/>
							<h:commandButton id="btnCancelar" onclick="#{confirm}" actionListener="#{acervoProducao.cancelar}" value="Cancelar"/>
						</td>
					</tr>
					</tfoot>
				</table>
			</td>
		</tr>
	</table>

</h:form>
		<br>



	<c:if test="${empty acervoProducao.resultado}">
		<center>
		<i>Nenhum resultado encontrado</i>
		</center>
	</c:if>

	<c:if test="${not empty acervoProducao.resultado}">

		<table class="listagem">
			<caption>Produções Encontradas (${ fn:length(acervoProducao.resultado) })</caption>
			<thead>
				<tr>
					<td width="40%"> Título </td>
					<td> Docente </td>
					<td> Produção </tD>
					<td> Ano </td>
					<td> Download </td>
				</tr>
			</thead>
			<tbody>
			
			<c:set var="tipoP" />
			<c:forEach items="#{acervoProducao.resultado}" var="producao" varStatus="status">
		
				<c:if test="${producao.tipoProducao.descricao != tipoP}">
					<c:set var="tipoP" value="${producao.tipoProducao.descricao}"/>
					<tr>
						<td class="subFormulario" colspan="5"> ${tipoP} </td>
					</tr>
				</c:if>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>  ${producao.titulo} </td>
					<td> <a href="${portalDocente.linkPaginaPublicaDocente }/${ producao.servidor.primeiroUsuario.login }"  target="_blank" title="Acesse a página pública deste docente" >${producao.servidor.pessoa.nome} - ${producao.servidor.unidade.sigla}</a> </td>
					<td> ${producao.tipoProducao.descricao} </td>
					<td> ${producao.anoReferencia} </td>
					<td align="center">
						<a href="/sigaa/verProducao?idProducao=${producao.idArquivo}&key=${ sf:generateArquivoKey(producao.idArquivo) }" target="_blank">
						  	<img src="/shared/img/icones/download.png" border="0" alt="Baixar Arquivo da Produção" title="Baixar Arquivo da Produção" />
						</a>
					</td>
				</tr>

			</c:forEach>
			</tbody>
		</table>

	</c:if>




</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>