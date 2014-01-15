<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.esquerda{text-align: left !important;}
	.direita{text-align: right !important;}
	.centro{text-align: center !important;}
	.instrutores{width: 26%;}
	.nome{width: 27%;}
	.periodo{width: 13%;}
</style>

<f:view>
	
	<a4j:keepAlive beanName="grupoAtividadesAP"></a4j:keepAlive>
	<h2 class="title">
		<ufrn:subSistema /> > Visualização do Grupo de Atividades de Atualização Pedagógica
	</h2>
	
	<center>
		<div class="infoAltRem">
		    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Baixar Arquivo
		</div>
	</center>
	
	<h:form id="visualizacaoGrupoAtividade">
	<table class="visualizacao" width="90%">
		<caption class="visualizacao">Grupo de Atividade</caption>

		<tr>
			<th width="15%">Denominação:</th>
			<td>
				<h:outputText value="#{grupoAtividadesAP.obj.denominacao}"/>	
			</td>
		</tr> 
		
		<tr>
			<th>Período:</th>
			<td>
				<h:outputText value="#{grupoAtividadesAP.obj.inicio}" ></h:outputText>	
					 a 
				<h:outputText value="#{grupoAtividadesAP.obj.fim}" ></h:outputText>
			</td>
		</tr>
	
					
		<%-- LISTA DAS ATIVIDADES PARA O GRUPO --%>
		<tr>
			<td colspan="2">	
				
				<table class="subListagem" width="100%">
					<caption>Atividades do Grupo</caption>
					<thead>
						<tr>
							<th class="esquerda nome">Nome</th>
							<th class="esquerda instrutores">Professores</th>
							<th class="centro periodo">Período</th>
							<th class="centro periodo">Horário</th>
							<th class="direita">CH</th>
							<th class="direita">Nº de Vagas</th>
							<th class="centro"></th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="#{grupoAtividadesAP.obj.atividadesAtivas}" var="ag" varStatus="i">
						<tr>
							<td class="esquerda nome"><h:outputText value="#{ag.nome}"/></td>
							<td class="esquerda instrutores"><h:outputText value="#{ag.descricaoInstrutores}"/></td>
							<td class="centro periodo"><h:outputText value="#{ag.descricaoPeriodo}"/></td>
							<td class="centro periodo">
								<h:outputText value="#{ag.horarioInicio} a #{ag.horarioFim}" rendered="#{not empty ag.horarioInicio}"/>
								<h:outputText value="Não Informado" rendered="#{empty ag.horarioInicio}"/>
							</td>
							<td class="direita">
								<h:outputText value="#{ag.ch}h" rendered="#{not empty ag.ch}"/>
								<h:outputText value="Não Informado" rendered="#{empty ag.ch}"/>
							</td>
							<td class="direita">
								<h:outputText value="#{ag.numVagas}" rendered="#{not empty ag.numVagas}"/>
								<h:outputText value="Não Informado" rendered="#{empty ag.numVagas}"/>
							</td>
							<td class="direita">
								<c:if test="${ag.idArquivo > 0}">
									<a href="/sigaa/verProducao?idProducao=${ag.idArquivo}&key=${ sf:generateArquivoKey(ag.idArquivo) }" title="Baixar Arquivo" target="_blank">
										 <img src="/shared/img/icones/download.png"/>
									</a>
								</c:if>
							</td>
						</tr>	
					</c:forEach>
					</tbody>
				</table>

			</td>
		</tr>
				
		</tbody>
	</table>
		
	<br/>
	<center>
		<h:commandLink value="<< Voltar" action="#{grupoAtividadesAP.listar}" 
		  immediate="true" id="btnvoltar">
		  	<f:param name="idGrupo" value="#{grupoAtividadesAP.obj.id}"/>
		</h:commandLink>	  
	</center>	
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
