<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>

    <ul>
    	<li>Manutenção de Coordenadores
         	<ul>
				<li><h:commandLink action="#{coordenacaoCurso.iniciar}" value="Identificar Coordenador" onclick="setAba('administracao')" id="identificarCoordenador"/></li>				
				<li><h:commandLink action="#{coordenacaoCurso.iniciarBuscaCoordenadorPorCurso}" value="Alterar/Substituir/Cancelar Coordenador" onclick="setAba('administracao')" id="substituirCancelarCoord"/></li>
				<li><h:commandLink action="#{coordenacaoCurso.listar}" value="Listar Coordenadores" onclick="setAba('administracao')" id="listarCoordenadores"/></li>				
				<li>
					<h:commandLink action="#{coordenacaoCurso.listar}" value="Logar como Coordenador" onclick="setAba('administracao')" id="logarCoordenador">
						<f:param name="logarComo" value="true" />
					</h:commandLink>
				</li>
			</ul>
        </li>
    
    	<li>Manutenção de Secretários
         	<ul>
         	<li> 
         		<h:commandLink action="#{secretariaUnidade.iniciarCoordenacaoLato}" value="Identificar Secretário" onclick="setAba('administracao')">
         			<f:param name="gestor" value="true" />
         		</h:commandLink>
         	 </li>
			<li> 
				<h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoCoordenacaoLato}" value="Substituir Secretário" onclick="setAba('administracao')">
					<f:param name="gestor" value="true" />
         		</h:commandLink> 
			</li>
			<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_curso.jsf?aba=administracao">Listar Secretários</a></li>
         	</ul>
        </li>
        <li>Docentes Externos
			<ul>
			<li> <h:commandLink action="#{docenteExterno.popular}" value="Cadastrar Docente Externo" onclick="setAba('administracao')"/> </li>
			<li> <h:commandLink action="#{docenteExterno.iniciarAlterar}" value="Consultar Docentes Externos" onclick="setAba('administracao')"/> </li>
			<li> <ufrn:checkRole	papeis="<%= new int[] {SigaaPapeis.GESTOR_LATO} %>">
					<h:commandLink action="#{docenteExterno.iniciarAlterar}" onclick="setAba('administracao')">Cadastrar Usuário Para Docente Externo</h:commandLink>
				</ufrn:checkRole></li>
			</ul>
		</li>    	
		<li>Administração Parâmetros Proposta
			<ul>
				<li> <h:commandLink action="#{parametrosPropostaCursoLatoMBean.preCadastrar}" value="Parâmetros Proposta Curso Lato" onclick="setAba('administracao')" id="calendario"/> </li>
			</ul>
		</li>
		<li>Mensalidades
         	<ul>
         		<li><h:commandLink action="#{relatoriosLato.iniciarRelatorioMensalidadesPagas}" value="Mensalidades Pagas" onclick="setAba('administracao')"/> </li>
         	</ul>
        </li>
        <li>Processo Seletivo
			<ul>
				<li><h:commandLink action="#{processoSeletivo.listar}" value="Gerenciar Processos Seletivos"  onclick="setAba('administracao')" id="cadastrarProcSeletivo"/> </li>
			</ul>
		</li>
	</ul>