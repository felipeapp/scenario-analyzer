<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="consultarMonitor" />
    <ufrn:checkNotRole papel="<%= SigaaPapeis.GESTOR_MONITORIA %>">
        <%@include file="/portais/discente/menu_discente.jsp"%>
    </ufrn:checkNotRole>

	<h2><ufrn:subSistema /> > Relatórios de Atividades do Monitor</h2>

	<c:if test="${acesso.monitoria}">
		<table class="visualizacao">
			<caption class="listagem">Dados do Discente Selecionado</caption>
			<tbody>
				<tr>
					<th><b>Matrícula:</b></th>
					<td>${atividadeMonitor.discente.matricula}</td>
				</tr>
				<tr>
					<th><b>Nome:</b></th>
					<td>${atividadeMonitor.discente.nome}</td>
				</tr>
				<tr>
					<th><b>Curso:</b></th>
					<td>${atividadeMonitor.discente.discente.curso.descricao}</td>
				</tr>
			</tbody>
		</table>
	</c:if>

	<br/>

	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
  		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
  		    <h:graphicImage url="/img/monitoria/document_new.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Cadastrar Novo" rendered="#{acesso.monitoria}"/>
  		    <br/>
	</div>
	
	<h:form>
	<table class="listagem" width="100">
	<caption>Lista de Relatórios de Atividades Cadastrados</caption>
	<thead>
	<tr>
		<td style="text-align: center" width="10%">Mês/Ano</td>
		<td style="text-align: center ">Data/Hora de Envio</td>
		<td>Atividades Desenvolvidas</td>
		<td style="text-align: center">Orientador(a) Validou</td>
		<td style="text-align: right" width="10%">Frequência</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	</thead>

	<c:set var="atividades" value="#{atividadeMonitor.atividadesDiscente}" />

	<c:if test="${empty atividades}">
            <tr> <td colspan="8" align="center"> <font color="red">Não há Relatórios de Atividades Cadastrados ou o Usuário atual não é Monitor de projetos ativos</font> </td></tr>
	</c:if>

    <c:set var="projeto" value="0" />
	<c:forEach items="#{atividades}" var="atividade" varStatus="status">
	   <c:if test="${projeto != atividade.discenteMonitoria.projetoEnsino.id}">
	       <c:set var="projeto" value="${atividade.discenteMonitoria.projetoEnsino.id}" />
            <tr>	
                 <td colspan="9" style="background: #C8D5EC; padding: 2px 0 2px 5px;"> <b>${atividade.discenteMonitoria.projetoEnsino.anoTitulo}</b><br/>
                 	<b>Vínculo:</b> <i>${ atividade.discenteMonitoria.tipoVinculo.descricao }</i><br/>
                 	<b>Período:</b> <i><fmt:formatDate value="${ atividade.discenteMonitoria.dataInicio }" pattern="dd/MM/yyyy" /> até <fmt:formatDate value="${ atividade.discenteMonitoria.dataFim }" pattern="dd/MM/yyyy" /></i>
                 </td>
	      </tr>
	      <c:if test="${atividade.id == 0}">
               <tr>
                   <td colspan="8"><center><font color="red">Não existem relatórios de atividades cadastrados.</font> </center></td>
               </tr>
          </c:if>
	   </c:if>
	   <c:if test="${atividade.id > 0}">
	       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td style="text-align: center" width="10%"> <fmt:formatNumber value="${atividade.mes}" pattern="00"/> / ${ atividade.ano }</td>
				<td style="text-align: center"> <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${atividade.dataCadastro}" /> </td>
				<c:if test="${fn: length(atividade.atividades) > 70 }"><td>${fn:substring(atividade.atividades,0,70)}...</td></c:if>
				<c:if test="${fn: length(atividade.atividades) < 70 }"><td>${atividade.atividades}</td></c:if>
				<td style="text-align: center"> ${atividade.validadoOrientador == true ? 'SIM':'NÃO'} </td>
				<td align="right" width="10%"> <c:if test="${atividade.validadoOrientador || acesso.monitoria}">${atividade.frequencia}%</c:if> </td>			
				<td width="2%"></td>
				<td width="2%">
						<c:if test="${not (atividade.validadoPrograd or atividade.validadoOrientador) and (empty atividade.dataValidacaoOrientador)}">
								<h:commandLink id="btAlterar" title="Alterar" action="#{atividadeMonitor.atualizar}" rendered="#{!atividade.validadoOrientador}" style="border: 0;" >
							       <f:param name="id" value="#{atividade.id}"/>
					               <h:graphicImage url="/img/alterar.gif" />
								</h:commandLink>
						</c:if>
				</td>
				<td width="2%">			
						<c:if test="${not (atividade.validadoPrograd or atividade.validadoOrientador) and (empty atividade.dataValidacaoOrientador)}">
								<h:commandLink id="btCancelar" title="Remover"  action="#{atividadeMonitor.preRemover}"  rendered="#{!atividade.validadoOrientador}" style="border: 0;">
							       <f:param name="id" value="#{atividade.id}"/>
					               <h:graphicImage url="/img/delete.gif" />
								</h:commandLink>
						</c:if>
				</td>		
				<td width="2%">		
						<h:commandLink id="btVisualizar"  title="Visualizar" action="#{atividadeMonitor.visualizarRelatorioMonitor}" 
							style="border: 0;">
						       <f:param name="id" value="#{atividade.id}"/>
				               <h:graphicImage url="/img/view.gif" />
						</h:commandLink>
				</td>
			</tr>
	     </c:if>
	   </c:forEach>
	   
	   <h:panelGroup rendered="#{acesso.monitoria}">
		   <tfoot>
				<tr>
					<td colspan="9">
						<center><h:commandButton  value="<< Voltar" action="#{comissaoMonitoria.novoRelatorioAtividades}" immediate="true"/></center>
					</td>
				</tr>
			</tfoot>
	   </h:panelGroup>
	   
	  </table>
    </h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>