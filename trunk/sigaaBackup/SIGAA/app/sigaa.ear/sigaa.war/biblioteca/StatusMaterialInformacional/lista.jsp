<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>



<f:view>
	<h2><ufrn:subSistema /> > Status dos Materiais Informacionais</h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listados os Status que os materiais informacionais do acervo podem possuir. </p>
    	<br/>
    	<p> <strong> Um Status indica uma caracterísca do material que o distiguirá na realização dos empréstimos.</strong> </p>
    	<p> Não podem existir dois os mais Status com a mesma descrição no sistema.</p>
		<p> Pelo Status também é possível definir se o material será emprestado e será levado em consideração na contagem de materais para a realização de reservas.</p>
		<br/>
		<p><strong>IMPORTANTE:</strong> Após cadastrar um novo Status, deve-se configurar na repectiva Política de Empréstimos os prazos e quantidades cujos materiais desse Status possuirão.</p>
	</div>
	
	<h:form>


		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
		
			<div class="infoAltRem" style="width:80%;">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{statusMaterialInformacionalMBean.preCadastrar}" value="Novo Status" />
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Status
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Status
			</div>
			
		</ufrn:checkRole>

		<table class="listagem" style="width:80%">
			<caption>Lista de Status do Materiais (${statusMaterialInformacionalMBean.size})</caption>
			
			<thead>
				<tr>
					<th style="width: 60%;">Descrição</th>
					<th style="width: 25%;text-align: center;">Permite Empréstimo</th>
					<th style="width: 25%;text-align: center;">Aceita Reserva</th>
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
						<th style="width: 5%;"></th>
						<th style="width: 5%;"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{statusMaterialInformacionalMBean.all}" var="s" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${s.descricao}</td>
					
					<td style="text-align: center;">
						<c:if test="${s.permiteEmprestimo}">
							<span style="color: green;">SIM</span>
						</c:if>
						<c:if test="${! s.permiteEmprestimo}">
							<span style="color: red;">NÃO </span>
						</c:if>
					</td>
					
					<td style="text-align: center;">
						<c:if test="${s.aceitaReserva}">
							<span style="color: green;">SIM</span>
						</c:if>
						<c:if test="${! s.aceitaReserva}">
							<span style="color: red;">NÃO </span>
						</c:if>
					</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
						<td>
							<h:commandLink title="Alterar Status" action="#{statusMaterialInformacionalMBean.preAtualizar}">
								<f:param name="id" value="#{s.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Status" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Remover Status" action="#{statusMaterialInformacionalMBean.preRemover}">
								<f:param name="id" value="#{s.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Status" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="5" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{statusMaterialInformacionalMBean.cancelar}"></h:commandButton>
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>