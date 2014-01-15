<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Resumos do Seminário de Iniciação à Docência - SID</h2>

	<h:outputText value="#{resumoSid.create}"/>
	
	<c:set var="participacoes" value="#{resumoSid.participacoes}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/pesquisa/certificado.png" style="overflow: visible;" /><h:outputText value=": Emitir Certificado"/>
    </div>
	<br/>

	<h:form>
	 <table class="listagem">
	    <caption>Participantes de Resumos SID Encontrados para o projeto selecionado</caption>

	      <thead>
	      	<tr>
	      		<th>Participou</th>
	      		<th>Apresentou</th>
	        	<th width="50%">Discente</th>
	        	<th>Curso</th>		        	
	        	<th>&nbsp;</th>		        			        	
	        </tr>
	 	</thead>
	 	<tbody>

		<c:set var="projeto" value=""/>
     	<c:forEach items="#{participacoes}" var="participacao" varStatus="status">

      			<c:if test="${ projeto != participacao.resumoSid.projetoEnsino.id }">
					<c:set var="projeto" value="${ participacao.resumoSid.projetoEnsino.id }"/>
       	
						<tr>
							<td colspan="5" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
									${participacao.resumoSid.projetoEnsino.anoTitulo}
							</td>
						</tr>						
				</c:if>
	       		       	
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${participacao.participou ? 'SIM' :'NÃO'} </td>
					<td> ${participacao.apresentou ? 'SIM' :'NÃO'} </td>
                    <td> ${participacao.discenteMonitoria.discente.pessoa.nome} </td>
					<td> ${participacao.discenteMonitoria.discente.curso.descricao} </td>
					<td>
						<c:if test="${participacao.participou and participacao.apresentou}">
							<h:commandLink  title="Emitir Certificado" action="#{ resumoSid.emitirCertificado }" id="btEmitirCertificado" styleClass="noborder">
							   	<f:param name="id" value="#{participacao.id}"/>				    	
								<h:graphicImage url="/img/pesquisa/certificado.png" />
							</h:commandLink>
						</c:if>
					</td>
              </tr>
          </c:forEach>
	   	   <c:if test="${empty participacoes}" >
		  		<tr><td colspan="5" align="center"><font color="red">Usuário atual não possui certificados pendentes de impressão.</font></td></tr>
		   </c:if>
	 	</tbody>
	 </table>
	 </h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>