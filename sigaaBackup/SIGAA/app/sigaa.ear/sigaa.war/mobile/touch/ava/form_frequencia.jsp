<%@include file="../include/cabecalho.jsp"%>

<meta name = "format-detection" content = "telephone=no">

<f:view>
	<a4j:keepAlive beanName="frequenciaAluno" />
	<div data-role="page" id="page-form-frequencia" data-theme="b">
		<h:form id="formLancarFrequencia">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink id="lnkVoltar" value="Voltar" action="#{ portalDocenteTouch.exibirCalendarios }" /></li>
					<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicio"/></li>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><small><strong><h:outputText value="#{ portalDocenteTouch.turma.disciplina.codigoNome} (#{portalDocenteTouch.turma.anoPeriodo})" escape="false" /></strong></small></p>
				
				<p align="center">
					<strong>Frequência <h:outputText value="#{portalDocenteTouch.dataSelecionada}" />
					<small>(Máx. de faltas: <c:out value="${portalDocenteTouch.maxFaltas}" />)</small></strong>
				</p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				
				<c:if test="${!portalDocenteTouch.semAula && (not empty portalDocenteTouch.frequencias)}">								  
					<div data-role="fieldcontain">
						<ul data-role="listview" data-inset="true" data-theme="b">
							<c:forEach varStatus="numero" var="freqAluno" items="#{portalDocenteTouch.frequencias}">
								<li class="${numero.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" style="border: 1px solid #939eae;" data-icon="false">
									<a>
									<label style="white-space: normal; width: 100%;" for="mat_${freqAluno.discente.matricula}">
										<b><c:out value="${numero.count}" />. </b>
										<b><h:outputText value="#{freqAluno.discente.pessoa.nome}" style="color: red;" rendered="#{freqAluno.faltas >= portalDocenteTouch.maxFaltas}" />
										<h:outputText value="#{freqAluno.discente.pessoa.nome}" rendered="#{freqAluno.faltas < portalDocenteTouch.maxFaltas}" /></b>
										<i><h:outputText value="(#{freqAluno.discente.matricula})" style="color: red;" rendered="#{freqAluno.faltas >= portalDocenteTouch.maxFaltas}" />
										<h:outputText value="(#{freqAluno.discente.matricula})" rendered="#{freqAluno.faltas < portalDocenteTouch.maxFaltas}" /></i><br/>
									</label>
									<label style="white-space: normal; width: 100%;">Faltas:</label><br/>
									<input type="range" name="mat_${freqAluno.discente.matricula}_idt_${portalDocenteTouch.turma.id}" id="mat_${freqAluno.discente.matricula}" 
										value="${freqAluno.faltas}" min="0" max="${portalDocenteTouch.maxFaltas}"/>
									</a>
								</li>
							</c:forEach>
						</ul>
						<h:commandLink id="btnGravar" action="#{portalDocenteTouch.cadastrarFrequencia }" rendered="#{ not empty portalDocenteTouch.frequencias }">Gravar</h:commandLink>
						<h:commandLink id="btnRemover" action="#{portalDocenteTouch.removerFrequencia }" rendered="#{ not empty portalDocenteTouch.frequencias }" onclick="if (!confirm('Tem certeza que deseja apagar as frequências para esta data?')) return false;">Remover</h:commandLink>
						<h:commandLink id="btnCancelar" action="#{portalDocenteTouch.exibirCalendarios }" onclick="if (!confirm('Tem certeza que deseja cancelar esta operação?')) return false;">Cancelar</h:commandLink>
						<input type="hidden" id="maxFaltas" value="${portalDocenteTouch.maxFaltas }"/>
					</div>
				</c:if>
			</div>

			<script>
				$("#formLancarFrequencia\\:btnGravar").attr("data-role", "button");
				$("#formLancarFrequencia\\:btnRemover").attr("data-role", "button");
				$("#formLancarFrequencia\\:btnCancelar").attr("data-role", "button");
				
				$("#formLancarFrequencia\\:lnkVoltar").attr("data-icon", "back");
				$("#formLancarFrequencia\\:lnkInicio").attr("data-icon", "home");
				$("#formLancarFrequencia\\:lnkSair").attr("data-icon", "sair");
			</script>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>	
		</h:form>
		
	  	<%@include file="../include/modo_classico.jsp"%>
	</div>
		  
  </f:view>
<%@include file="/mobile/touch/include/rodape.jsp"%>
