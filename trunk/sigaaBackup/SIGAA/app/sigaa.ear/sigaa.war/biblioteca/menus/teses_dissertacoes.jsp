<%@ taglib prefix="h"    uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_GESTOR_BDTD, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
<ul>
	<li>Publicação
		<ul>
			<li>
				<h:commandLink action="#{consultarDefesaMBean.iniciar}" onclick="setAba('teses_dissertacoes')" value="Catalogar Teses/Dissertações"> 
					<f:param name="catalogar" value="true"/>
				</h:commandLink> 
				<ufrn:help> 
					<p> Cataloga as Teses e Dissertações no acervo das bibliotecas.</p>
					<br/>
					<p> Caso a catalogação da Tese/Dissertação já exista, é possível alterar as suas informação por essa opção. </p>
					<br/>
					<p> <strong>Observação:</strong> Só é possível verificar se a catalogação existe caso ela seja catalogada por essa operação, ou pela operação existente no setor de processos  técnicos: <i> Processos Técnicos -> Catalogação -> Catalogar Defesa </i> </p>
				</ufrn:help>
			</li>
			<%-- Só pode ir para produção depois da tarefa #43902 Fluxo de Publicação de Tese/Dissertação
			<li>
				<h:commandLink action="#{termoPublicacaoTD.iniciarPublicacao}" onclick="setAba('teses_dissertacoes')" value="Teses/Dissertações Pendentes de Publicação na BDTD " />
				<blink style="color:red;font-weight: bold;">( ${termoPublicacaoTD.pendentePublicacao} )</blink>
			</li>
			--%>			
		</ul>
	</li>
	
	<li>Consultas
		<ul>
			<%-- Só pode ir para produção depois da tarefa #43902 Fluxo de Publicação de Tese/Dissertação
			<li><h:commandLink action="#{termoPublicacaoTD.buscarDiscente}" onclick="setAba('teses_dissertacoes')" value="Buscar/Listar Solicitações (TEDE)" /></li>
			--%>
			<li><h:commandLink action="#{consultarDefesaMBean.iniciar}" onclick="setAba('teses_dissertacoes')" value="Consultar Bancas" /></li>						
		</ul>
	</li>
</ul>
</ufrn:checkRole>