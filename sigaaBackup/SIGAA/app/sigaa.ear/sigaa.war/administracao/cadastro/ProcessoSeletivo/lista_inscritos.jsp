<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/shared/javascript/tablekit/style.css"/>
<script type="text/javascript" src="/shared/loadScript?src=javascript/tablekit/fastinit.js"></script>
<script type="text/javascript" src="/shared/loadScript?src=javascript/tablekit/tablekit.js"></script>
<style>
	.aprovada {
		color: #292;		
		font-weight: bold;
	}
	.number{
		text-align:center !important;
	}
	.colNome{ 
		width:54%;
		text-align: left;
	}
	.colInscricao{
		text-align: right !important;
		width: 8%;
	}
	.colCpf{
		text-align: center !important;
		width: 12%;
	}
	.colStatus{
		text-align: center !important;
	}
	.colIcone{
		width: 12%;
		text-align: right;
	}
		.colIcone img{
			padding-left:5px;
		}
	.rodape{
		text-align: center;
	}
</style>

<f:view>
	<%--  <%@include file="/stricto/menu_coordenador.jsp"%> --%>
	<c:set var="inscritos" value="#{processoSeletivo.obj.inscritos}"/>

	<h:form id="formBuscaInscritos">

		<h2>
			<ufrn:subSistema /> > 
			<h:commandLink action="#{processoSeletivo.listar}" value="Processos Seletivos"/> >  
			Lista de Inscritos
		</h2>
	
		<a4j:outputPanel id="visualizacaoInscritosProcessoSeletivo">	
		<table class="visualizacao">
			<caption>Dados do Processo Seletivo</caption>

			<c:choose>
				<%-- SE PROCESSO SELETIVO CURSO LATOS, PÓS E TÉCNICO --%>
				<c:when test="${not empty processoSeletivo.obj.curso}">
					<tr>
						<th>Curso:</th>
						<td>${processoSeletivo.obj.curso.descricao}</td>
					</tr>
					<tr>
						<th>Nível:</th>
						<td>${processoSeletivo.obj.curso.nivelDescricao}</td>
					</tr>
				</c:when>
				<%-- SE PROCESSO SELETIVO FOR PARA TRANSFERÊNCIA VOLUNTÁRIA --%>
				<c:otherwise>
					<tr>
						<th>Curso:</th>
						<td>${processoSeletivo.obj.matrizCurricular.curso.descricao}</td>
					</tr>
					<tr>
						<th>Nível:</th>
						<td>${processoSeletivo.obj.matrizCurricular.curso.nivelDescricao}</td>
					</tr>
				</c:otherwise>
			</c:choose>
			<tr>
				<th>Período de Inscrições:</th>
				<td><ufrn:format type="data"
					valor="${processoSeletivo.obj.editalProcessoSeletivo.inicioInscricoes}"></ufrn:format> a <ufrn:format
					type="data" valor="${processoSeletivo.obj.editalProcessoSeletivo.fimInscricoes}"></ufrn:format>
				</td>
			</tr>
	
			<tr>
				<th>Número de Inscritos:</th>
				<td>${fn:length(processoSeletivo.obj.inscritos)}</td>
			</tr>
			<tr>
				<th>Número de Aprovados:</th>
				<td><span style="font-size: 1.3em; color: #292;"> <b>${processoSeletivo.obj.numeroAprovados}</b>
				</span></td>
			</tr>
		</table>
		</a4j:outputPanel>
		<br clear="all"/>
		
		<center>
			
			<table class="formulario">
				<caption>Filtrar Inscrições</caption>
				<tbody>
					
					<tr>
						<td></td>
						<th>Status da Inscrição:</th>
						<td>
							<h:selectOneMenu value="#{processoSeletivo.statusInscricao}" id="filtroStatusInscricao" >
								<f:selectItem itemLabel="-- TODOS --" itemValue="" />
								<f:selectItems value="#{inscricaoSelecao.comboStatus}" />
							</h:selectOneMenu>
						</td>
					</tr>
					
				</tbody>
				<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton action="#{processoSeletivo.buscarInscritos}" id="BuscarInscricaoSelecao" value="Buscar"/>
							<h:commandButton action="#{processoSeletivo.listar}" immediate="true"
								id="CancelarBuscainscricaoSelecao" value="<< Voltar"/>
							<h:inputHidden value="#{processoSeletivo.obj.id}"/>
						</td>
					</tr>
				</tfoot>
			</table>
			<br clear="all"/>
			
			<c:if  test="${not empty inscritos}">
				<div class="infoAltRem">
					<h:commandLink title="Notificar Inscritos" id="notificarInscritos"
							 action="#{notificarInscritos.iniciar}">
							<f:setPropertyActionListener value="#{inscritos}" target="#{notificarInscritos.inscritos}"/>
							<h:graphicImage value="/img/notificar.png"/> Notificar Inscritos
					</h:commandLink>
					<f:verbatim><h:graphicImage value="/img/view.gif" style="overflow: visible;" />: </f:verbatim>Visualizar Dados do Candidato 
					<f:verbatim><h:graphicImage value="/img/user_edit.png" style="overflow: visible;" />: </f:verbatim>Cadastrar Discente
					<f:verbatim><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: </f:verbatim>Alterar Dados da Inscrição
					<c:if test="${ processoSeletivo.obj.editalProcessoSeletivo.taxaInscricao > 0 }" ><br/>
						<h:graphicImage value="/img/pagamento.png" style="overflow: visible;" />: GRU Quitada
						<h:graphicImage value="/img/imprimir.gif" style="overflow: visible;" />: Reimprimir a GRU
					</c:if>
				</div>
			</c:if>
			
		</center>
		</h:form>
		
		<h:form id="formListaInscritos">
			
			<h:dataTable value="#{inscritos}" var="item" rendered="#{not empty inscritos}"  styleClass="listagem" 
				width="100%" rowClasses="linhaPar, linhaImpar" footerClass="rodape"
				columnClasses="colInscricao,colNome,colCpf,colStatus,colIcone" >
				
				<f:facet name="caption"><f:verbatim>Lista de Inscritos (${fn:length(inscritos)})</f:verbatim></f:facet>
				
				<h:column headerClass="colInscricao">
					<f:facet name="header"><f:verbatim>Inscrição</f:verbatim></f:facet>
					<h:outputText value="#{item.numeroInscricao}"/>
				</h:column>
				
				<h:column>
					<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
					<h:outputText value="#{item.pessoaInscricao.nome}"/>
				</h:column>
				
				<h:column>
					<f:facet name="header"><f:verbatim>CPF/PASSAPORTE</f:verbatim></f:facet>
					<h:outputText value="#{item.pessoaInscricao.cpf}" converter="convertCpf" rendered="#{not empty item.pessoaInscricao.cpf && empty item.pessoaInscricao.passaporte}" />
					<h:outputText value="#{item.pessoaInscricao.passaporte}" rendered="#{not empty item.pessoaInscricao.passaporte}"  />
				</h:column>
				
				<h:column headerClass="colStatus">
						<f:facet name="header"><f:verbatim>Status</f:verbatim></f:facet>
						<a4j:region>
							<h:selectOneMenu rendered="true" id="selecaoStatus" 
									valueChangeListener="#{inscricaoSelecao.registrarStatus}" value="#{item.status}" >
									<f:selectItems  value="#{inscricaoSelecao.comboStatus}"/>
									<a4j:support event="onchange" reRender="iconesInscritos,visualizacaoInscritosProcessoSeletivo">
										<f:param name="id" value="#{item.id}" />
									</a4j:support>
							</h:selectOneMenu>
					
							<a4j:status onstop="alert('O status do inscrito foi alterado com sucesso!');" >
								<f:facet name="start" >
									<h:graphicImage value="/img/indicator.gif" />
								</f:facet>
							</a4j:status>
						</a4j:region>
				</h:column>
				
				<h:column>
					
					<f:facet name="header"><f:verbatim> </f:verbatim></f:facet>
					<a4j:outputPanel id="iconesInscritos">	
						<h:graphicImage url="/img/pagamento.png" rendered="#{ item.gruQuitada }" title="GRU Quitada" />
						
						<h:commandLink title="Reimprimir a GRU" id="reimprimirGRU" immediate="true" action="#{inscricaoSelecao.imprimirGRU}" 
							rendered="#{ processoSeletivo.obj.editalProcessoSeletivo.taxaInscricao > 0 && !item.gruQuitada }">
								<h:graphicImage url="/img/imprimir.gif" rendered="#{ !item.gruQuitada }" title="Reimprimir a GRU" />
								<f:param name="id" value="#{item.id}" />
						</h:commandLink>
						
						<h:commandLink title="Visualizar Dados do Candidato" id="visualizarDados" immediate="true" action="#{processoSeletivo.viewInscrito}">
								<h:graphicImage url="/img/view.gif" />
								<f:param name="id" value="#{item.id}" />
						</h:commandLink>	
						
						<h:commandLink title="Cadastrar Discente" id="cadastrarDiscente" rendered="#{item.aprovada}" 
									action="#{inscricaoSelecao.cadastrarDiscente}">
									<h:graphicImage url="/img/user_edit.png" />
									<f:setPropertyActionListener value="#{item.pessoaInscricao.id}" 
										target="#{inscricaoSelecao.obj.pessoaInscricao.id}"/>
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>
						<h:graphicImage  rendered="#{!item.aprovada}" 
									title="Cadastrar Discente"  url="/img/user_edit_off.png" />
						
						<h:commandLink title="Alterar Dados da Inscrição" id="editalInscricao" action="#{inscricaoSelecao.atualizar}" immediate="true">
									<h:graphicImage url="/img/alterar.gif" />
									<f:setPropertyActionListener value="#{item.pessoaInscricao.id}" 
										target="#{inscricaoSelecao.obj.pessoaInscricao.id}"/>
								<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</a4j:outputPanel>
				</h:column>	
					
				<f:facet name="footer">
					<f:verbatim >&nbsp;</f:verbatim>
				</f:facet>
				
			</h:dataTable>
	

		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>