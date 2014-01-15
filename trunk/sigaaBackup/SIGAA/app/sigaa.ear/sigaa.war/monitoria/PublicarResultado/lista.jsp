<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
<a4j:keepAlive beanName="publicarResultado" />

	<h:form id="formAtividade">
	
		<h2><ufrn:subSistema /> &gt;Publicar Resultado das Avalia��es</h2>
		
		<!-- Projetos -->
		<c:if test="${not empty publicarResultado.projetosAvaliados}">


			<div class="descricaoOperacao">
				<b>Aten��o:</b>
				Prezado Gestor, por favor verifique o resultado abaixo. Este � o primeiro passo para o resultado final de um edital.<br/>
				Ele representa uma pr�via do resultado das avalia��es que ser� publicado. 
				Entrentanto a M�dia publicada nesta tela n�o representa a M�dia Final do projeto. Observe que a Nota Final do projeto � composta por
				outros fatores (ver edital). <br/>
				A lista abaixo determina somente a M�dia de An�lise do projeto e se o mesmo foi ou n�o Recomendado pela Comiss�o de Avalia��o.
			</div>
		
			
			<br />
			<div class="infoAltRem">
		  		<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Projeto de Ensino
		    </div>
		    <br />
			<table class="listagem">
				<caption class="listagem">Lista de Projetos Avaliados </caption>
				<thead>
					<tr> 
						<th>Ano</th>
						<th width="70%">T�tulo</th>
						<th>Situa��o</th>
						<th style="text-align: right;">M�dia</th>
						<th>Resultado</th>
						<th></th>														
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{publicarResultado.projetosAvaliados}" var="pm" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${pm.projeto.ano} </td>
						<td> ${pm.projeto.titulo} </td>
						<td> ${pm.projeto.situacaoProjeto.descricao}</td>			
						<td style="text-align: right;"> ${pm.mediaAnalise}	</td>
						<td> ${pm.mediaAnalise >= pm.editalMonitoria.mediaAprovacaoProjeto ? 'RECOMENDADO' : 'N�O RECOMENDADO'}	</td>
						<td width="2%">
								<h:commandLink  title="Visualizar Projeto de Ensino" action="#{ projetoMonitoria.view }" id="btView">
								      <f:param name="id" value="#{pm.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>
				</c:forEach>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="6" align="center">
		 				<h:commandButton action="#{ publicarResultado.publicarResultadoProjetos }" value="Publicar Resultado"/>
						<h:commandButton value="<< Voltar" action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}" immediate="true" />
						<h:commandButton action="#{ publicarResultado.cancelar }" value="Cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</c:if>
		<!-- FIM DOS Projetos -->
		
		<c:if test="${empty publicarResultado.projetosAvaliados}">
			<center><font color='red'>N�o h� avalia��es de projetos para publica��o.</font>
				<br />
			 	<br />
				<h:commandButton value="<< Voltar" action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}" immediate="true" />
			</center>
		</c:if>
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>